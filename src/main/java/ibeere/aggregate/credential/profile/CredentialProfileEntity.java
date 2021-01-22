package ibeere.aggregate.credential.profile;

import org.springframework.beans.factory.annotation.Autowired;
import ibeere.aggregate.credential.CredentialId;
import ibeere.event.EventPublisher;
import ibeere.aggregate.profile.AbstractProfileEntity;
import ibeere.user.UserId;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import static org.springframework.web.context.support.SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext;

/** Profile in a credential context */
@Entity
@Table(name ="profile_entity")
@EntityListeners(CredentialProfileEntity.Listener.class)
public class CredentialProfileEntity extends AbstractProfileEntity {

    @OneToMany(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "user_id")
    private List<TemplateCredentialEntity> credentials = new ArrayList<>();

    public CredentialProfileEntity(UserId id, List<TemplateCredentialEntity> credentials) {
        super(id);
        this.credentials = credentials;
    }

    // hibernate
    public CredentialProfileEntity() {
    }

    public void updateUserGenCredentialIfPresent(CredentialId credentialId, String text) {
        this.getCredentials().stream()
                .filter(x -> x.getCredentialId().equals(credentialId))
                .filter(x -> x.getType().isUserGenerated())
                .findAny()
                .ifPresent(c -> c.update(text));
    }

    public void removeUserGenCredentialIfPresent(CredentialId credentialId) {
        this.getCredentials().stream()
                .filter(x -> x.getCredentialId().equals(credentialId))
                .filter(x -> x.getType().isUserGenerated())
                .findAny()
                .ifPresent(c -> this.getCredentials()
                        .removeIf(entity -> entity.getCredentialId().equals(credentialId)));
    }

    public void addUserGenCredentialIfNoClash(TemplateCredentialEntity newEntity) {
        if (this.getCredentials().stream()
                .filter(cred -> cred.getType().equals(newEntity.getType()))
                .noneMatch(cred -> cred.getType().isUserGenerated())) {
            this.getCredentials().add(newEntity);
        }
    }

    public List<TemplateCredentialEntity> getCredentials() {
        return credentials;
    }

    static class Listener {
        @Autowired
        private EventPublisher eventPublisher;

        @PrePersist void onPrePersist(Object o) { }
            @PostPersist void onPostPersist(CredentialProfileEntity entity) {
            processInjectionBasedOnCurrentContext(this);
                eventPublisher.credentialProfileUpdated(entity.getId());
        }
        @PostLoad void onPostLoad(Object o) {}
        @PreUpdate void onPreUpdate(Object o) {}
        @PostUpdate void onPostUpdate(CredentialProfileEntity entity) {
            processInjectionBasedOnCurrentContext(this);
            eventPublisher.credentialProfileUpdated(entity.getId());
        }
        @PreRemove void onPreRemove(Object o) {}
        @PostRemove void onPostRemove(CredentialProfileEntity entity) {
            processInjectionBasedOnCurrentContext(this);
            eventPublisher.credentialProfileUpdated(entity.getId());
        }
    }
}
