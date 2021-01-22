package ibeere.aggregate.question;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import ibeere.audience.Audience;
import ibeere.ddd.RootEntity;
import ibeere.event.EventPublisher;
import ibeere.user.UserId;

import javax.persistence.*;
import java.time.Instant;

import static org.springframework.web.context.support.SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext;
import static ibeere.audience.Audience.ANONYMOUS;

/**
 * A question that can be asked by a user.
 */
@javax.persistence.Entity
@EntityListeners(QuestionEntity.Listener.class)
@Getter
public class QuestionEntity implements RootEntity<QuestionId> {
    @EmbeddedId
    private QuestionId questionId;

    @Column(nullable = false)
    private UserId userId;
    @Column(nullable = false)
    private String userName;
    @Column(columnDefinition = "text", nullable = false, length = 10485760)
    private String questionText;
    private String linkText;
    @Column(nullable = false, unique = true)
    private String path;
    private Instant submitDate;
    private Audience audience;
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "q_and_a_user_id"))
    })
    private UserId qandAUserId;

    @Column(columnDefinition = "text", length = 10485760)
    private String rejectionReason;

    public QuestionEntity(QuestionId questionId, UserId userId, String userName, String questionText,
                          String linkText,
                          String path,
                          Instant submitDate,
                          Audience audience,
                          UserId qandAUserId, String rejectionReason) {
        this.questionId = questionId;
        this.userId = userId;
        this.userName = userName;
        this.questionText = questionText;
        this.linkText = linkText;
        this.path = path;
        this.submitDate = submitDate;
        this.audience = audience;
        this.qandAUserId = qandAUserId;
        this.rejectionReason = rejectionReason;
    }

    // hibernate
    public QuestionEntity() {
    }

    public String getUserName() {
        if (this.audience == ANONYMOUS) {
            return "Anonymous";
        }
        return userName;
    }

    @Override
    public QuestionId getId() {
        return questionId;
    }

    static class Listener {

        @Autowired
        private EventPublisher eventPublisher;

        @PrePersist void onPrePersist(Object o) {}
        @PostPersist void onPostPersist(QuestionEntity questionEntity) {
            processInjectionBasedOnCurrentContext(this);
            eventPublisher.questionAsked(questionEntity.getId());
        }
        @PostLoad void onPostLoad(Object o) {}
        @PreUpdate void onPreUpdate(Object o) {}
        @PreRemove void onPreRemove(Object o) {}
        @PostRemove void onPostRemove(Object o) {}
    }
}
