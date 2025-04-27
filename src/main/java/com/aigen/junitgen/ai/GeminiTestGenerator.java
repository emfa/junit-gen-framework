package com.aigen.junitgen.ai;

import com.aigen.junitgen.model.AIInput;
import com.aigen.junitgen.prompt.PromptAugmentor;
import com.aigen.junitgen.scan.ClassIndex;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;

public class GeminiTestGenerator implements AITestGenerator {

    private final String apiKey;
    private final OpenAIPromptBuilder promptBuilder = new OpenAIPromptBuilder(); // reusing the same prompt format
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public GeminiTestGenerator(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public String generateTestClass(AIInput input, boolean debugPrompt, ClassIndex classIndex) {
        String prompt = promptBuilder.buildPrompt(input);

        prompt = PromptAugmentor.enrichPrompt(input,classIndex,prompt);

        if (debugPrompt) {
            System.out.println("\n--- BEGIN PROMPT ---\n");
            System.out.println(prompt);
            System.out.println("\n--- END PROMPT ---\n");
        }

        try {
            HttpRequest request = buildRequest(prompt);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return extractGeneratedCode(response.body());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "// Gemini API error: " + e.getMessage();
        }
    }

    private HttpRequest buildRequest(String prompt) {
        JSONObject contentPart = new JSONObject().put("parts", new JSONArray().put(new JSONObject().put("text", prompt)));
        JSONArray contents = new JSONArray().put(contentPart);

        JSONObject payload = new JSONObject()
                .put("contents", contents);

        return HttpRequest.newBuilder()
                .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();
    }

    private String extractGeneratedCode(String json) {
        System.out.println("🧾 Gemini Raw Response:\n" + json);

        JSONObject root = new JSONObject(json);
        JSONArray candidates = root.optJSONArray("candidates");

        if (candidates != null && !candidates.isEmpty()) {
            JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
            JSONArray parts = content.optJSONArray("parts");

            if (parts != null && !parts.isEmpty()) {
                return parts.getJSONObject(0).optString("text", "").trim();
            }
        }

        return "// No output from Gemini";
    }
}
