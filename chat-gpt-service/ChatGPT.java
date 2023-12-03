package com.starchat.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * ChatGPT service for interacting with chat.
 * <br/>
 * Implementation of the {@link com.intersystems.gateway.Service} which serves as s link between the IRIS.
 */
public class ChatGPT implements com.intersystems.gateway.Service {

    private static final String CHAT_GPT_URI = "https://api.openai.com/v1/chat/completions";

    @Override
    public byte[] execute(byte[] bytes) {
        String gprMessage = new String(bytes, StandardCharsets.UTF_8);

        String response = this.executeRequest(gprMessage);

        return response.getBytes(StandardCharsets.UTF_8);
    }

    private String executeRequest(final String message) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CHAT_GPT_URI))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer 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")
                    .POST(HttpRequest.BodyPublishers.ofString("{\n" +
                            "     \"model\": \"gpt-3.5-turbo\",\n" +
                            "     \"messages\": [{\"role\": \"user\", \"content\": \"" + message + "\"}],\n" +
                            "     \"temperature\": 0.7\n" +
                            "   }")).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBodyText = response.body();

            System.out.println("Response body: " + responseBodyText);

            JsonElement responseJson = new JsonParser().parseString(responseBodyText);

            return responseJson.getAsJsonObject()
                    .getAsJsonArray("choices")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("message")
                    .getAsJsonPrimitive("content")
                    .getAsString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}