package com.example.codeflow.controller;

import com.example.codeflow.domain.search.aievaluation.ChatRequest;
import com.example.codeflow.service.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping("/api/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody ChatRequest request) {
        String reply = chatClient.chat(request.getMessages());
        return ResponseEntity.ok(Map.of("reply", reply));
    }
}
