package ibeere.aggregate.question;

import ibeere.audience.Audience;
import ibeere.ddd.ImmutableEntity;
import ibeere.page.common.LastUpdatedContent;
import ibeere.aggregate.question.answer.Answer;
import ibeere.aggregate.question.answer.AnswerId;
import ibeere.user.UserId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static java.time.LocalDate.now;
import static java.time.Period.between;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static ibeere.support.Constants.STANDARD_ZONE;
import static ibeere.support.Utils.getDaysAgoToHuman;
import static ibeere.audience.Audience.ANONYMOUS;
import static ibeere.aggregate.question.Score.*;

public class Question implements LastUpdatedContent, ImmutableEntity {
    private static final Random RANDOM = new Random(0);

    private final QuestionId questionId;
    private final String questionText;
    private final String path;
    private final String userName;
    private final UserId userId;
    private final long followingCount;
    private final Instant submitDate;
    private final List<Answer> answers;
    private final List<Answer> allAnswers;
    private final Audience audience;
    private final long randomValue = RANDOM.nextInt(5);
    private Instant lastFollowed;
    private String lastFollowedHumanReadable;

    public Question(QuestionId questionId, String questionText, String path, String userName, UserId userId, long followingCount, Instant submitDate, List<Answer> answers,
                    Audience audience) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.path = path;
        this.userId = userId;
        this.followingCount = followingCount;
        this.submitDate = submitDate;
        this.answers = Optional.ofNullable(answers)
                .map(a -> a.stream()
                        .filter(removableAnswer -> !removableAnswer.removed())
                        .sorted(comparing(Answer::getUpVoteCounts))
                        .collect(toList()))
                .orElse(null);
        this.allAnswers = Optional.ofNullable(answers)
                .map(a -> a.stream()
                        .sorted(comparing(Answer::getUpVoteCounts).reversed())
                        .collect(toList()))
                .orElse(null);
        this.audience = audience;

        if (audience == ANONYMOUS) {
            this.userName = "Anonymous";
        } else {
            this.userName = userName;
        }
    }

    // TODO Find a better solution for this
    void postInstance(Instant lastFollowed) {
        this.lastFollowed = lastFollowed;
        this.lastFollowedHumanReadable = lastFollowed == null ? null : getDaysAgoToHuman(
                lastFollowed);
    }

    public UserId getUserId() {
        return userId;
    }

    public long getFollowingCount() {
        return followingCount;
    }

    public Long getAnswerCount() {
        return Long.valueOf(Optional.ofNullable(answers).map(List::size).orElse(0));
    }

    public String getUrl() {
        return "https://ibeere.com" + "/question/" + path;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public List<Answer> getAllAnswers() {
        return allAnswers;
    }

    public boolean hasAnswers() {
        return answers.size() > 0;
    }

    public Answer getFirstAnswer() {
       return answers.stream()
                .filter(Answer::isNewAnswer)
                .findAny()
                .orElseGet(() -> answers.stream()
                        .max(comparing(Answer::getUpVoteCounts))
                        .orElse(null));
    }

    public QuestionId getQuestionId() {
        return questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getPath() {
        return path;
    }

    @Override
    public Instant getUpdated() {
        return submitDate;
    }

    public String getUserName() {
        return userName;
    }

    public String getLastFollowedHumanReadable() {
        return lastFollowedHumanReadable;
    }

    public Instant getLastFollowed() {
        return lastFollowed;
    }

    public Score getScore() {
        if (getAnswerCount() == 0) {
            if (this.isReallyNewQuestion())
                return REALLY_NEW_QUESTION;
            if (this.isNewQuestion())
                return NEW_QUESTION;
            return randomValue % 2 == 0 ? HIGH : LOW;
        } else {
            return getFirstAnswer().getScore();
        }
    }

    public Instant getSubmitDate() {
        return submitDate;
    }

    public Optional<Answer> answerById(AnswerId answerId) {
        return this.getAnswers().stream().filter(i -> i.getAnswerId().equals(answerId)).findFirst();
    }

    public Audience getAudience() {
        return audience;
    }


    public boolean isNewQuestion() {
        return between(submitDate.atZone(STANDARD_ZONE).toLocalDate(),
                now(STANDARD_ZONE)).getDays() <= 10;
    }

    public boolean isReallyNewQuestion() {
        return between(submitDate.atZone(STANDARD_ZONE).toLocalDate(),
                now(STANDARD_ZONE)).getDays() <= 2;
    }
}
