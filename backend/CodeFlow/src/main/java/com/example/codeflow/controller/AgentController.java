package com.example.codeflow.controller;

import com.example.codeflow.agent.BlogAgentService;
import com.example.codeflow.domain.search.aievaluation.ChatRequest;
import com.example.codeflow.domain.search.aievaluation.Message;
import com.example.codeflow.model.ChatSession;
import com.example.codeflow.service.ChatHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class AgentController {

    private final BlogAgentService blogAgentService;
    private final ChatHistoryService chatHistoryService;

    public AgentController(BlogAgentService blogAgentService, ChatHistoryService chatHistoryService) {
        this.blogAgentService = blogAgentService;
        this.chatHistoryService = chatHistoryService;
    }

    @PostMapping("/api/agent")
    public ResponseEntity<Map<String, Object>> agent(@RequestBody ChatRequest request) {
        return handleAgent(request, "NIJIKA", false);
    }

    @PostMapping("/api/agent/nazuna")
    public ResponseEntity<Map<String, Object>> agentNazuna(@RequestBody ChatRequest request) {
        return handleAgent(request, "NAZUNA", true);
    }

    @GetMapping("/api/agent/sessions")
    public ResponseEntity<List<Map<String, Object>>> listSessions(@RequestParam(defaultValue = "NIJIKA") String agent) {
        return ResponseEntity.ok(chatHistoryService.listSessions(agent.toUpperCase()));
    }

    @GetMapping("/api/agent/sessions/{id}/messages")
    public ResponseEntity<List<Map<String, String>>> getMessages(@PathVariable Long id) {
        return ResponseEntity.ok(chatHistoryService.loadMessages(id));
    }

    @DeleteMapping("/api/agent/sessions/{id}")
    public ResponseEntity<Map<String, String>> deleteSession(@PathVariable Long id) {
        chatHistoryService.deleteSession(id);
        return ResponseEntity.ok(Map.of("message", "Session deleted"));
    }

    private ResponseEntity<Map<String, Object>> handleAgent(ChatRequest request, String agentType, boolean nazuna) {
        ChatSession session = chatHistoryService.getOrCreateSession(agentType, request.getSessionId());

        List<Message> msgs = request.getMessages();
        if (msgs != null && !msgs.isEmpty()) {
            Message last = msgs.get(msgs.size() - 1);
            if ("user".equals(last.getRole())) {
                chatHistoryService.saveMessage(session, agentType, "user", last.getContent());
                // Auto-title: use first user message (truncated)
                if (msgs.size() == 1 || (msgs.size() == 2 && msgs.get(0).getRole().equals("assistant"))) {
                    String title = last.getContent();
                    if (title.length() > 30) title = title.substring(0, 30) + "...";
                    chatHistoryService.updateTitle(session.getId(), title);
                }
            }
        }

        String systemPrompt = nazuna ? BlogAgentService.NAZUNA_SYSTEM_PROMPT : null;
        String reply = nazuna
                ? blogAgentService.run(msgs, systemPrompt)
                : blogAgentService.run(msgs);

        chatHistoryService.saveMessage(session, agentType, "assistant", reply);

        return ResponseEntity.ok(Map.of("reply", reply, "sessionId", session.getId()));
    }
}
