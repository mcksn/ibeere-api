package ibeere.aggregate.credential.profile;

import org.hibernate.annotations.Where;
import org.springframework.beans.factory.annotation.Autowired;
import ibeere.aggregate.credential.AbstractCredentialEntity;
import ibeere.aggregate.credential.CredentialId;
import ibeere.aggregate.credential.CredentialType;
import ibeere.event.EventPublisher;
import ibeere.user.UserId;

import javax.persistence.*;

import static org.springframework.web.context.support.SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext;

/**
 * Template credential is a credential of a user that can be used to create a
 * {@link ibeere.aggregate.credential.CredentialEntity}.
 * This credential are not attached to an answer.
 */
@Entity
@Table(name ="credential_entity")
@Where(clause = "answer_id is null")
@EntityListeners(TemplateCredentialEntity.Listener.class)
public class TemplateCredentialEntity extends AbstractCredentialEntity {
    public TemplateCredentialEntity(CredentialId credentialId, UserId userId, CredentialType type, String text) {
        super(credentialId, userId, type, text);
    }

    //hibernate
    public TemplateCredentialEntity() {
    }

    public void update(String text) {
        this.text = text;
    }

    static class Listener {
        @Autowired
        private EventPublisher eventPublisher;

        @PrePersist void onPrePersist(Object o) {}
        @PostPersist void onPostPersist(TemplateCredentialEntity entity) {
            processInjectionBasedOnCurrentContext(this);
            // TODO use a templateCredentialCreated event that can mapped to this event
            eventPublisher.credentialProfileUpdated(entity.getUserId());
        }
        @PostLoad void onPostLoad(Object o) {}
        @PreUpdate void onPreUpdate(Object o) {}
        @PostUpdate void onPostUpdate(TemplateCredentialEntity entity) {
            processInjectionBasedOnCurrentContext(this);
            // TODO use a templateCredentialUpdated event that can mapped to this event
            eventPublisher.credentialProfileUpdated(entity.getUserId());
        }
        @PreRemove void onPreRemove(Object o) {}
        @PostRemove void onPostRemove(TemplateCredentialEntity entity) {
            processInjectionBasedOnCurrentContext(this);
            // TODO use a templateCredentialRemoved event that can mapped to this event
            eventPublisher.credentialProfileUpdated(entity.getUserId());
        }
    }
}
