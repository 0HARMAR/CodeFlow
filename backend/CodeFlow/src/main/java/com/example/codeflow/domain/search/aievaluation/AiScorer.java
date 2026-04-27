package com.example.codeflow.domain.search.aievaluation;

/**
 * AI评分枚举，表示不同的评分等级
 */
public enum AiScorer {
    VERY_LOW,   // 0
    LOW,        // 1
    MEDIUM,     // 2
    HIGH,       // 3
    VERY_HIGH   // 4
    ;

    // 预定义数组用于快速索引查找
    private static final AiScorer[] VALUES = values();

    /**
     * 根据整数值获取对应的枚举值
     * @param value 整数值 (0-4)
     * @return 对应的AiScorer枚举值
     * @throws IllegalArgumentException 当value不在有效范围内时抛出
     */
    public static AiScorer fromInt(int value) {
        if (value < 0 || value >= VALUES.length) {
            throw new IllegalArgumentException("Invalid value: " + value + ", expected range [0-" + (VALUES.length - 1) + "]");
        }
        return VALUES[value];
    }

    public Double getScore() {
        switch (this) {
            case VERY_LOW:
                return 0.0;
            case LOW:
                return 0.5;
            case MEDIUM:
                return 1.0;
            case HIGH:
                return 1.5;
            case VERY_HIGH:
                return 2.0;
            default:
                return 0.0;
        }
    }
}
