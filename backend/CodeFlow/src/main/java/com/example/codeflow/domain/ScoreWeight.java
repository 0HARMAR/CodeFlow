package com.example.codeflow.domain;

public class ScoreWeight {

    public final double length;
    public final double like;
    public final double comment;
    public final double freshness;

    public ScoreWeight(double length, double like, double comment, double freshness) {
        this.length = length;
        this.like = like;
        this.comment = comment;
        this.freshness = freshness;
    }
}
