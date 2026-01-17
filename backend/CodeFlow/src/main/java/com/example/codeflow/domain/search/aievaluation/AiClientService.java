package com.example.codeflow.domain.search.aievaluation;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class AiClientService {

    private final RestClient restClient;

    public AiClientService() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:3000")
                .build();
    }

    public String chat(List<Message> messages) {

        ChatRequest request = new ChatRequest(messages);

        ChatResponse response = restClient.post()
                .uri("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(ChatResponse.class);

        return response != null ? response.getReply() : null;
    }
}
