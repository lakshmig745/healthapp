package com.example.healthapp.service;

import com.example.healthapp.ai.GeminiClient;
import com.example.healthapp.dto.FoodGuidanceResponse;
import com.example.healthapp.entity.ConditionSource;
import com.example.healthapp.entity.User;
import com.example.healthapp.entity.UsersHealth;
import com.example.healthapp.repository.UsersHealthRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodGuidanceServiceImp implements AiFoodGuidanceService {

    private final GeminiClient geminiClient;
    private final UsersHealthRepo usersHealthRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FoodGuidanceServiceImp(
            GeminiClient geminiClient,
            UsersHealthRepo usersHealthRepo
    ) {
        this.geminiClient = geminiClient;
        this.usersHealthRepo = usersHealthRepo;
    }

    // =====================================================
    // MAIN METHOD CALLED BY CONTROLLER
    // =====================================================
    @Override
    public FoodGuidanceResponse getGuidanceForUser(User user) {

        int age = LocalDate.now().getYear() - user.getDob().getYear();

        String knownConditions = buildKnownConditionsString(user);
        String riskConcerns = buildRiskConcernsString(user);

        // ---------------- FIX 1: HARDENED PROMPT ----------------
        String prompt = """
You are a nutrition assistant.

User profile:
- Gender: %s
- Age: %d
- Known health conditions: %s
- Risk-based concerns: %s

Task:
1. List foods the user should AVOID.
2. List foods the user SHOULD EAT.
3. Keep recommendations general (food categories).
4. Do NOT give medical advice.
5. Risk based concerns are not existing health conditions but they has higher chance of developing them in future. They don't need to completely avoid the foods but can be careful with some.

IMPORTANT OUTPUT RULES:
- Return ONLY raw JSON
- Do NOT use markdown
- Do NOT use ``` or ```json
- Do NOT add any text before or after the JSON
- Do NOT explain the JSON

JSON format:
{
  "avoid": [string],
  "recommended": [string],
  "notes": [string]
}
""".formatted(
                user.getGender(),
                age,
                knownConditions.isEmpty() ? "None" : knownConditions,
                riskConcerns.isEmpty() ? "None" : riskConcerns
        );

        // Call Gemini
        JsonNode geminiResponse = geminiClient.generateContent(prompt);

        // Extract AI text
        JsonNode textNode =
                geminiResponse.at("/candidates/0/content/parts/0/text");

        if (textNode.isMissingNode()) {
            throw new RuntimeException("Gemini response missing text");
        }

        // ---------------- FIX 2: SANITIZE AI OUTPUT ----------------
        String rawText = textNode.asText();
        String cleanJson = extractPureJson(rawText);

        try {
            JsonNode rootNode = objectMapper.readTree(cleanJson);

            // ---- avoid ----
            List<String> avoid = new ArrayList<>();
            if (rootNode.has("avoid") && rootNode.get("avoid").isArray()) {
                for (JsonNode n : rootNode.get("avoid")) {
                    avoid.add(n.asText());
                }
            }

            // ---- recommended ----
            List<String> recommended = new ArrayList<>();
            if (rootNode.has("recommended") && rootNode.get("recommended").isArray()) {
                for (JsonNode n : rootNode.get("recommended")) {
                    recommended.add(n.asText());
                }
            }

            // ---- notes (STRING OR ARRAY → STRING) ----
            String notes = "";
            if (rootNode.has("notes")) {
                JsonNode notesNode = rootNode.get("notes");
                if (notesNode.isTextual()) {
                    notes = notesNode.asText();
                } else if (notesNode.isArray()) {
                    notes = notesNode.findValuesAsText("")
                            .stream()
                            .collect(Collectors.joining(" "));
                }
            }

            return new FoodGuidanceResponse(avoid, recommended, notes);

        } catch (Exception e) {
            throw new RuntimeException("Invalid AI response format", e);
        }
    }


    // =====================================================
    // PRIVATE HELPER METHODS
    // =====================================================

    private String buildKnownConditionsString(User user) {

        List<UsersHealth> conditions =
                usersHealthRepo.findByUserId(user.getId());

        return conditions.stream()
                .filter(c -> c.getConditionsource() == ConditionSource.KNOWN)
                .map(c -> c.getHealthcondition().getName())
                .collect(Collectors.joining(", "));
    }

    private String buildRiskConcernsString(User user) {

        List<UsersHealth> conditions =
                usersHealthRepo.findByUserId(user.getId());

        return conditions.stream()
                .filter(c -> c.getConditionsource() == ConditionSource.RISK_BASED)
                .map(c -> c.getHealthcondition().getName())
                .collect(Collectors.joining(", "));
    }

    // ---------------- SANITIZER (MANDATORY FOR LLMs) ----------------
    private String extractPureJson(String aiText) {

        String cleaned = aiText.trim();

        // Remove markdown code fences if present
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceAll("^```[a-zA-Z]*", "");
            cleaned = cleaned.replaceAll("```$", "");
            cleaned = cleaned.trim();
        }

        return cleaned;
    }
}