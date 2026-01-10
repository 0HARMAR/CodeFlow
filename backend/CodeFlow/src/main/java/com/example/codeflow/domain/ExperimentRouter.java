package com.example.codeflow.domain;

public class ExperimentRouter {

    public static ExperimentGroup route(Long userId) {
        int hash = Math.abs(Long.hashCode(userId));
        return (hash % 2 == 0)
                ? ExperimentGroup.A
                : ExperimentGroup.B;
    }
}
