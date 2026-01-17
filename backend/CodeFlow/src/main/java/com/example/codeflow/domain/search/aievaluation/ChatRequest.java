package com.example.codeflow.domain.search.aievaluation;

import java.util.List;

public class ChatRequest {

    private List<Message> messages;

    public ChatRequest() {}

    public ChatRequest(List<Message> messages) {
        this.messages = messages;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
