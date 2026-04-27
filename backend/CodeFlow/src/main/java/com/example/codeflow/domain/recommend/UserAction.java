package com.example.codeflow.domain.recommend;

public enum UserAction {

    // 曝光未点击（弱负反馈 / 或直接忽略）
    IMPRESSION(0, 0.0, false),

    // 点击
    CLICK(1, 1.0, true),

    // 有效阅读（如 >= 30s）
    READ(2, 2.0, true),

    // 点赞 / 收藏（强正反馈）
    FAVORITE(3, 3.0, true);

    /**
     * 行为等级（用于训练 label / 日志）
     */
    private final int level;

    /**
     * 行为权重（用于用户偏好向量）
     */
    private final double weight;

    /**
     * 是否是正反馈
     */
    private final boolean positive;

    UserAction(int level, double weight, boolean positive) {
        this.level = level;
        this.weight = weight;
        this.positive = positive;
    }

    public int getLevel() {
        return level;
    }

    public double getWeight() {
        return weight;
    }

    public boolean isPositive() {
        return positive;
    }
}
