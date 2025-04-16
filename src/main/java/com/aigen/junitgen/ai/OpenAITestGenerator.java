package com.aigen.junitgen.ai;

import com.aigen.junitgen.model.AIInput;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;

import org.json.JSONArray;
import org.json.JSONObject;

public class OpenAITestGenerator implements AITestGenerator {

    private final String apiKey;
    private final String model;
    private final OpenAIPromptBuilder promptBuilder = new OpenAIPromptBuilder();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public OpenAITestGenerator(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = model != null ? model : "gpt-3.5-turbo";
    }

    @Override
    public String generateTestClass(AIInput input) {
        String prompt = promptBuilder.buildPrompt(input);

        try {
            HttpRequest request = buildRequest(prompt);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return extractGeneratedCode(response.body());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "// Error: " + e.getMessage();
        }
    }

    private HttpRequest buildRequest(String prompt) {
        JSONObject payload = new JSONObject();
        payload.put("model", model);
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "user").put("content", prompt));
        payload.put("messages", messages);
        payload.put("temperature", 0.3);

        return HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();
    }

    private String extractGeneratedCode(String responseJson) {
        JSONObject json = new JSONObject(responseJson);

        if (json.has("error")) {
            JSONObject error = json.getJSONObject("error");
            return "// OpenAI Error: " + error.optString("message", "Unknown error");
        }

        JSONArray choices = json.optJSONArray("choices");
        if (choices != null && !choices.isEmpty()) {
            return choices.getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim();
        }
        return "// No valid completion received from OpenAI.";
    }
}
