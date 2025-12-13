package com.example.backend_cs.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class GeminiService {

    private final String apiKey;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public static final String MODEL = "gemini-flash-latest";

    public GeminiService(@Value("${GEMINI_API_KEY}") String apiKey) {
        this.apiKey = apiKey;
    }

    public String askGemini(String prompt) {
        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                    + MODEL + ":generateContent?key=" + apiKey;

            String requestBody = """
            {
              "contents": [
                {
                  "parts": [
                    { "text": "%s" }
                  ]
                }
              ]
            }
            """.formatted(prompt.replace("\"", "'"));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = mapper.readTree(response.body());

            JsonNode textNode = root
                    .path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text");

            if (!textNode.isMissingNode()) {
                return textNode.asText();
            }

            return "Unexpected Gemini response format: " + response.body();

        } catch (Exception e) {
            e.printStackTrace();
            return "Sorry, I couldn't reach Gemini right now.";
        }
    }
}

