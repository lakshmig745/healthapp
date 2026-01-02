package com.example.healthapp.service;

import com.example.healthapp.ai.GeminiClient;
import com.example.healthapp.dto.MealPlanResponse;
import com.example.healthapp.entity.ConditionSource;
import com.example.healthapp.entity.User;
import com.example.healthapp.entity.UsersHealth;
import com.example.healthapp.repository.UsersHealthRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MealPlanServiceImp implements AiMealPlanService{

    private final GeminiClient geminiClient;
    private final UsersHealthRepo usersHealthRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MealPlanServiceImp(GeminiClient geminiClient, UsersHealthRepo usersHealthRepo) {
        this.geminiClient = geminiClient;
        this.usersHealthRepo = usersHealthRepo;
    }

    @Override
    public MealPlanResponse getMealPlanForUser(User user) {

        int age = LocalDate.now().getYear() - user.getDob().getYear();

        String knownConditions = buildKnownConditionsString(user);
        String riskConcerns = buildRiskConcernsString(user);

        // ---------------- FIX 1: HARDENED PROMPT ----------------
        String prompt = """
You are a nutrition assistant.

User profile:
- Gender: %s
- Age: %d
- Race: %s
- Known health conditions, They have these health conditions already: %s
- Risk-based concerns, They don't have these conditions currently but are at risk of having these conditions in future : %s

Task:
1. Give Healthy Meal plan based on their Race, mainly from their cuisine for a week.
2. Give each day meal plan separately including breakfast, Snacks, Lunch,Dinner.
3. Meal Plan food should not be same continuously every day
4. Make the Meal plan with Foods user can eat and try to avoid foods he/she shouldn't eat
5. Do NOT give medical advice.

IMPORTANT OUTPUT RULES:
- Return ONLY raw JSON
- Do NOT use markdown
- Do NOT use ``` or ```json
- Do NOT add any text before or after the JSON
- Do NOT explain the JSON

JSON format:
{
  "weekPlan": {
    "Monday": {
      "breakfast": "string",
      "lunch": "string",
      "snacks": "string",
      "dinner": "string"
    },
    "Tuesday": { },
    "Wednesday": { },
    "Thursday": { },
    "Friday": { },
    "Saturday": { },
    "Sunday": { }
  },
  "notes": "string"
}
""".formatted(
                user.getGender(),
                age,
                user.getRace(),
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

            // ---------- DEFENSIVE KEY RESOLUTION ----------
            JsonNode weekNode =
                    rootNode.has("weekPlan") ? rootNode.get("weekPlan") :
                            rootNode.has("weekPlan") ? rootNode.get("weekPlan") :
                                    rootNode.has("mealPlan") ? rootNode.get("mealPlan") :
                                            null;

            if (weekNode == null || !weekNode.isObject()) {
                throw new RuntimeException("AI response missing week plan object");
            }

            Map<String, Map<String, String>> weekPlan = new LinkedHashMap<>();

            Iterator<String> days = weekNode.fieldNames();
            while (days.hasNext()) {
                String day = days.next();
                JsonNode mealsNode = weekNode.get(day);

                Map<String, String> meals = new LinkedHashMap<>();
                meals.put("breakfast", mealsNode.path("breakfast").asText(""));
                meals.put("snacks", mealsNode.path("snacks").asText(""));
                meals.put("lunch", mealsNode.path("lunch").asText(""));
                meals.put("dinner", mealsNode.path("dinner").asText(""));

                weekPlan.put(day, meals);
            }

            return new MealPlanResponse(weekPlan);

        } catch (Exception e) {
            throw new RuntimeException("Invalid AI meal plan format", e);
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
