package ibeere.aggregate.comment;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ibeere.event.EventPublisher;
import ibeere.aggregate.question.answer.AnswerId;
import ibeere.aggregate.question.QuestionId;
import ibeere.user.UserId;

import javax.persistence.*;
import java.time.Instant;

import static org.springframework.web.context.support.SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext;

/**
 * Comment made by a user on an answer
 */
@Entity
@Getter
@EntityListeners(CommentEntity.Listener.class)
public class CommentEntity implements ibeere.ddd.Entity<CommentId> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentEntity.class);

    @EmbeddedId
    private CommentId commentId;

    @Embedded
    private AnswerId answerId;

    @Embedded
    private QuestionId questionId;

    @Column(nullable = false)
    private UserId userId;

    @Column(columnDefinition = "text", nullable = false, length = 10485760)
    private String content;

    private Instant submitDate;

    public CommentEntity(CommentId commentId, AnswerId answerId, QuestionId questionId, UserId userId, String content, Instant submitDate) {
        this.commentId = commentId;
        this.answerId = answerId;
        this.questionId = questionId;
        this.userId = userId;
        this.content = content;
        this.submitDate = submitDate;
    }

    // hibernate
    CommentEntity() {
    }

    public UserId getAuthorId() {
        return userId;
    }

    @Override
    public CommentId getId() {
        return commentId;
    }

    static class Listener {

        @Autowired
        private EventPublisher eventPublisher;

        @PrePersist
        void onPrePersist(Object o) {
        }

        @PostPersist
        void onPostPersist(CommentEntity commentEntity) {
            processInjectionBasedOnCurrentContext(this);
            // TODO use commentMade event that maps to this event
            eventPublisher.questionDocUpdated(commentEntity.questionId);
        }

        @PostLoad
        void onPostLoad(Object o) {
        }

        @PreUpdate
        void onPreUpdate(Object o) {
        }

        @PreRemove
        void onPreRemove(Object o) {
        }

        @PostRemove
        void onPostRemove(CommentEntity commentEntity) {
            processInjectionBasedOnCurrentContext(this);
            LOGGER.info("deleting comment {}", commentEntity);
            // TODO use commentRemoved event that maps to this event
            eventPublisher.questionDocUpdated(commentEntity.questionId);
        }
    }
}
