package ibeere.aggregate.question.follow;

import ibeere.aggregate.question.QuestionId;
import ibeere.event.EventPublisher;
import ibeere.user.UserId;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

import static org.springframework.web.context.support.SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext;

@javax.persistence.Entity
@Getter
@EntityListeners(FollowEntity.Listener.class)
public class FollowEntity implements ibeere.ddd.Entity {
    @Id
    private UUID id;
    private UserId userId;
    private QuestionId questionId;
    private Instant submitDate;

    public FollowEntity(UUID id,
                        UserId userId,
                        QuestionId questionId,
                        Instant submitDate) {
        this.id = id;
        this.userId = userId;
        this.questionId = questionId;
        this.submitDate = submitDate;
    }

    // hibernate
    public FollowEntity() {
    }

    static class Listener {

        @Autowired
        private EventPublisher eventPublisher;

        @PrePersist
        void onPrePersist(Object o) { }
        @PostPersist
        void onPostPersist(FollowEntity entity) {
            processInjectionBasedOnCurrentContext(this);
            eventPublisher.questionFollowed(entity.getQuestionId(), entity.userId);
        }
        @PostLoad
        void onPostLoad(Object o) { }
        @PreUpdate
        void onPreUpdate(Object o) { }
        @PreRemove
        void onPreRemove(Object o) { }
        @PostRemove
        void onPostRemove(FollowEntity entity) {
            eventPublisher.questionUnfollowed(entity.getQuestionId(), entity.userId);
        }
    }
}
