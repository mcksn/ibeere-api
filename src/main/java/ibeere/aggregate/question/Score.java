package ibeere.aggregate.question;

public enum Score {
    LOW(ScoreCategory.LOW),
    MID(ScoreCategory.MID),
    HIGH(ScoreCategory.HIGH),
    NEW_QUESTION(ScoreCategory.HIGH),
    REALLY_NEW_QUESTION(ScoreCategory.HIGH),
    NEW_ANSWER(ScoreCategory.HIGH),
    REALLY_NEW_ANSWER(ScoreCategory.HIGH),
    HIGH_RANDOM_VOTED_ANSWER(ScoreCategory.HIGH),
    LOW_RANDOM_VOTED_ANSWER(ScoreCategory.LOW),
    MID_REALLY_NEW_ANSWER_CONTENT(ScoreCategory.MID),
    MID_NEW_ANSWER_CONTENT(ScoreCategory.MID);

    private final ScoreCategory scoreCategory;

    Score(ScoreCategory scoreCategory) {
        this.scoreCategory = scoreCategory;
    }

    public ScoreCategory getCategory() {
        return scoreCategory;
    }

    public enum ScoreCategory {
        LOW,
        MID,
        HIGH
    }
}
