package com.example.codeflow.controller;

import com.example.codeflow.domain.search.aievaluation.ChatRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.codeflow.domain.search.aievaluation.AiClientService;

import java.util.Map;

@RestController
public class ChatController {

    private final AiClientService aiClientService;

    public ChatController(AiClientService aiClientService) {
        this.aiClientService = aiClientService;
    }

    @PostMapping("/api/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody ChatRequest request) {
        String reply = aiClientService.chat(request.getMessages());
        return ResponseEntity.ok(Map.of("reply", reply));
    }
}