package com.example.codeflow.controller;

import com.example.codeflow.agent.BlogAgentService;
import com.example.codeflow.domain.search.aievaluation.ChatRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AgentController {

    private final BlogAgentService blogAgentService;

    public AgentController(BlogAgentService blogAgentService) {
        this.blogAgentService = blogAgentService;
    }

    @PostMapping("/api/agent")
    public ResponseEntity<Map<String, String>> agent(@RequestBody ChatRequest request) {
        String reply = blogAgentService.run(request.getMessages());
        return ResponseEntity.ok(Map.of("reply", reply));
    }
}