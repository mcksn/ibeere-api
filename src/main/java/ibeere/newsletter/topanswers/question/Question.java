package ibeere.newsletter.topanswers.question;

import ibeere.ddd.RootEntity;
import ibeere.aggregate.question.QuestionId;

import javax.persistence.EmbeddedId;

/**
 * Question in 'top answers newsletter' context.
 *
 * Not really needed at this moment but written ahead of time for storing top-answers related attributes
 */
@javax.persistence.Entity
public class Question implements RootEntity<QuestionId> {

    @EmbeddedId
    private QuestionId id;

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
}
