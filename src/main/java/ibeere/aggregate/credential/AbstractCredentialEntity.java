package ibeere.aggregate.credential;

import ibeere.ddd.Entity;
import ibeere.user.UserId;

import javax.persistence.*;

/**
 * Correctness says there is no need for this entity to exist.
 * It's here for convenience and to serve as a reminder that {@link ibeere.aggregate.credential.profile.TemplateCredentialEntity}
 * and {@link CredentialEntity} share the same table. Again, for correctness they should have different tables.
 * Keeping the it this way now to handle changes to credentials that might impact both credential types.
 * TODO split table
 */
@MappedSuperclass
public class AbstractCredentialEntity implements Entity<CredentialId> {

    @EmbeddedId
    private CredentialId credentialId;

    @Embedded
    private UserId userId;

    @Enumerated(EnumType.STRING)
    private CredentialType type;

    protected String text;

    public AbstractCredentialEntity(CredentialId credentialId, UserId userId, CredentialType type, String text) {
        this.userId = userId;
        this.type = type;
        this.text = text;
        this.credentialId = credentialId;
    }

    public AbstractCredentialEntity() {
    }

    public UserId getUserId() {
        return userId;
    }

    public CredentialType getType() {
        return type;
    }

    public CredentialId getCredentialId() {
        return credentialId;
    }

    public String getText() {
        return text;
    }

    @Override
    public CredentialId getId() {
        return credentialId;
    }
}
