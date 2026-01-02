package com.example.healthapp.service;

import com.example.healthapp.ai.GeminiClient;
import com.example.healthapp.dto.AlternateRecepieRequest;
import com.example.healthapp.dto.AlternateRecepieResponse;
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
public class AlternateRecepieServiceImp implements AiAlternateRecepieService{
    private final GeminiClient geminiClient;
    private final UsersHealthRepo usersHealthRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AlternateRecepieServiceImp(GeminiClient geminiClient, UsersHealthRepo usersHealthRepo) {
        this.geminiClient = geminiClient;
        this.usersHealthRepo = usersHealthRepo;
    }

    @Override
    public AlternateRecepieResponse getAlternateRecepieForUser(User user, AlternateRecepieRequest recepiesrequest){
        int age = LocalDate.now().getYear() - user.getDob().getYear();

        String knownConditions = buildKnownConditionsString(user);
        String riskConcerns = buildRiskConcernsString(user);

        String prompt = """
You are a nutrition assistant.

User profile:
- Gender: %s
- Age: %d
- Race: %s
- Dish: %s
- Known health conditions, They have these health conditions already: %s
- Risk-based concerns, They don't have these conditions currently but are at risk of having these conditions in future : %s

Task:
1. Take the Dish name from user.
2. See if the Ingredients can be consumed by user based on his health conditions. Also keep in mind about his Risk based concerns.
3. If he cannot consume certain Ingredients, then suggest other alternate Ingredients to make the Dish.
4. Also give the steps to make the recipe.
5. Do NOT give medical advice.

IMPORTANT OUTPUT RULES:
- Return ONLY raw JSON
- Do NOT use markdown
- Do NOT use ``` or ```json
- Do NOT add any text before or after the JSON
- Do NOT explain the JSON

JSON format:
{
  "ingredients": [
    "string",
    "string"
  ],
  "steps": [
    "string",
    "string"
  ]
}
""".formatted(
                user.getGender(),
                age,
                user.getRace(),
                recepiesrequest.getDish(),
                knownConditions.isEmpty() ? "None" : knownConditions,
                riskConcerns.isEmpty() ? "None" : riskConcerns
        );

        // Calling Gemini
        JsonNode geminiResponse = geminiClient.generateContent(prompt);

        // Extracting AI text
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


            List<String> ingredients = new ArrayList<>();
            if (rootNode.has("ingredients") && rootNode.get("ingredients").isArray()) {
                for (JsonNode n : rootNode.get("ingredients")) {
                    ingredients.add(n.asText());
                }
            }


            List<String> steps = new ArrayList<>();
            if (rootNode.has("steps") && rootNode.get("steps").isArray()) {
                for (JsonNode n : rootNode.get("steps")) {
                    steps.add(n.asText());
                }
            }

            return new AlternateRecepieResponse(ingredients,steps);

        } catch (Exception e) {
            throw new RuntimeException("Invalid AI response format", e);
        }

    }


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
