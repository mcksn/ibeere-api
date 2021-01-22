package ibeere.aggregate.question.answer;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import ibeere.audience.Audience;
import ibeere.ddd.Entity;
import ibeere.event.EventPublisher;
import ibeere.aggregate.credential.CredentialId;
import ibeere.aggregate.question.QuestionId;
import ibeere.user.UserId;

import javax.persistence.*;
import java.time.Instant;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static javax.persistence.FetchType.EAGER;
import static org.springframework.web.context.support.SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext;

/**
 * AnswerEntity is an entity of the Question aggregate but has its own service independent of question service.
 * Decide whether to fully commit to AnswerEntity being its own aggregate root.
 */
@javax.persistence.Entity
@Getter
@EntityListeners(AnswerEntity.Listener.class)
public class AnswerEntity implements Entity<AnswerId> {
    @EmbeddedId
    private AnswerId answerId;

    @Embedded
    private QuestionId questionId;
    @Column(nullable = false)
    private UserId userId;
    private Audience audience;
    @ElementCollection(fetch = EAGER)
    private Set<CredentialId> credentials = new HashSet<>();
    private String userBio;
    private boolean editable;
    @Lob
    private String editorState; // TODO rename to content. Maybe with its own type
    private Instant submitDate;
    private int viewCount;
    private String path;

    public AnswerEntity(AnswerId answerId,
                        QuestionId questionId,
                        UserId userId,
                        Audience audience,
                        List<CredentialId> credentials,
                        String editorState,
                        Instant submitDate,
                        int viewCount,
                        String path,
                        String userBio, boolean editable) {
        this.answerId = answerId;
        this.questionId = questionId;
        this.userId = userId;
        this.audience = audience;
        this.credentials = new HashSet<>(credentials);
        this.editorState = editorState;
        this.submitDate = submitDate;
        this.viewCount = viewCount;
        this.path = path;
        this.userBio = userBio;
        this.editable = editable;
    }

    // hibernate
    public AnswerEntity() {
    }

    public void view() {
        this.viewCount = this.viewCount + 1;
    }

    // for util
    public void updateSubmitDate(Instant instant) {
        this.submitDate = instant;
    }

    public void updateContent(String content) {
        this.editorState = content;
    }

    public AnswerQuestionRef answerQuestionRef(){
        return AnswerQuestionRef.of(answerId, questionId);
    }

    @Override
    public AnswerId getId() {
        return answerId;
    }

    static class Listener {

        @Autowired
        private EventPublisher eventPublisher;

        @PrePersist void onPrePersist(Object o) {}
        @PostPersist void onPostPersist(AnswerEntity answerEntity) {
            processInjectionBasedOnCurrentContext(this);
            eventPublisher.questionAnswered(answerEntity.answerQuestionRef());
        }
        @PostLoad void onPostLoad(Object o) {}
        @PreUpdate void onPreUpdate(Object o) {}
        @PostUpdate void onPostUpdate(AnswerEntity answerEntity) {
            processInjectionBasedOnCurrentContext(this);
            // TODO use answerUpdated event that will map to this event
            eventPublisher.questionDocUpdated(answerEntity.answerQuestionRef().questionId());
        }
        @PreRemove void onPreRemove(Object o) {}
        @PostRemove void onPostRemove(Object o) {}
    }

}