package ibeere.newsletter.newquestion.question;

import lombok.Getter;
import ibeere.ddd.RootEntity;
import ibeere.aggregate.question.QuestionId;

import javax.persistence.EmbeddedId;
import java.time.Instant;

import static java.time.Instant.now;
import static ibeere.support.ClockProvider.STANDARD_CLOCK;

/** Question in 'new questions newsletter' context */
@javax.persistence.Entity
public class Question implements RootEntity<QuestionId> {

    @EmbeddedId
    private QuestionId id;

    @Getter
    private Boolean sentForNewQuestions;

    @Getter
    private Instant sentForNewQuestionsTime;

    public Question(QuestionId id) {
        this.id = id;
    }

    // hibernate
    public Question() {
    }

    @Override
    public QuestionId getId() {
        return id;
    }

    /**
     * Question is marked as having been sent out in newsletter.
     */
    public void sentOut() {
        sentForNewQuestions = true;
        sentForNewQuestionsTime = now(STANDARD_CLOCK);
    }
}
