package com.example.codeflow.service;

import com.example.codeflow.model.ChatMessage;
import com.example.codeflow.model.ChatSession;
import com.example.codeflow.repository.ChatMessageRepository;
import com.example.codeflow.repository.ChatSessionRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatHistoryService {

    private final ChatSessionRepository sessionRepo;
    private final ChatMessageRepository messageRepo;

    public ChatHistoryService(ChatSessionRepository sessionRepo, ChatMessageRepository messageRepo) {
        this.sessionRepo = sessionRepo;
        this.messageRepo = messageRepo;
    }

    public ChatSession createSession(String agentType, String title) {
        return sessionRepo.save(new ChatSession(agentType, title));
    }

    public ChatSession getOrCreateSession(String agentType, Long sessionId) {
        if (sessionId != null) {
            return sessionRepo.findById(sessionId).orElseGet(() -> createSession(agentType, "新的对话"));
        }
        return createSession(agentType, "新的对话");
    }

    public void saveMessage(ChatSession session, String agentType, String role, String content) {
        messageRepo.save(new ChatMessage(session, agentType, role, content));
        sessionRepo.save(session); // updates updatedAt via @PreUpdate
    }

    public List<Map<String, Object>> listSessions(String agentType) {
        return sessionRepo.findByAgentTypeOrderByUpdatedAtDesc(agentType)
                .stream()
                .map(s -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", s.getId());
                    m.put("title", s.getTitle());
                    m.put("createdAt", s.getCreatedAt() != null ? s.getCreatedAt().toString() : null);
                    m.put("updatedAt", s.getUpdatedAt() != null ? s.getUpdatedAt().toString() : null);
                    return m;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, String>> loadMessages(Long sessionId) {
        return messageRepo.findBySessionIdOrderByCreatedAtAsc(sessionId)
                .stream()
                .map(msg -> {
                    String mappedRole = "assistant".equals(msg.getRole()) ? "ai" : msg.getRole();
                    return Map.of("role", mappedRole, "content", msg.getContent());
                })
                .collect(Collectors.toList());
    }

    public void deleteSession(Long sessionId) {
        messageRepo.deleteBySessionId(sessionId);
        sessionRepo.deleteById(sessionId);
    }

    public void updateTitle(Long sessionId, String title) {
        sessionRepo.findById(sessionId).ifPresent(s -> {
            s.setTitle(title);
            sessionRepo.save(s);
        });
    }
}
