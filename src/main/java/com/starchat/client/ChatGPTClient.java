package com.starchat.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class ChatGPTClient {

    @Value("#{systemEnvironment['CHATGPT_API_KEY']}")
    private String API_ENDPOINT;

    @Value("#{systemEnvironment['CHAT_GPT_URI']}")
    private String CHAT_GPT_URI;

    public String sendMessage(String message) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CHAT_GPT_URI))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_ENDPOINT)
                    .POST(HttpRequest.BodyPublishers.ofString("{\n" +
                            "     \"model\": \"gpt-3.5-turbo\",\n" +
                            "     \"messages\": [{\"role\": \"user\", \"content\": \"" + message + "\"}],\n" +
                            "     \"temperature\": 0.7\n" +
                            "   }")).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.body());

            return jsonResponse
                    .get("choices")
                    .get(0)
                    .get("message")
                    .get("content")
                    .asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error communicating with the server.";
        }
    }
}
