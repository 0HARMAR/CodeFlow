package com.example.codeflow.domain.recommend;

public class ScoreWeightFactory {

    public static ScoreWeight getWeight(ExperimentGroup group) {
        if (group == ExperimentGroup.B) {
            // experient group: improve new content weight
            return new ScoreWeight(
                    0.5,
                    1.8,
                    2.5,
                    1.5
            );
        }

        // control group
        return new  ScoreWeight(
                0.5,
                2.0,
                3.0,
                1.0
        );
    }
}
