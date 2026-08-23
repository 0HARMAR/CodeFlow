package com.example.codeflow.repository;

import com.example.codeflow.model.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    List<ChatSession> findByAgentTypeOrderByUpdatedAtDesc(String agentType);
}
