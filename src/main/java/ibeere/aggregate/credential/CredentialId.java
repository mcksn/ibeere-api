package ibeere.aggregate.credential;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ibeere.ddd.Identifier;
import ibeere.framework.IdentifierDeConverter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.UUID;

@Access(AccessType.PROPERTY)
@Embeddable
@JsonDeserialize(converter = IdentifierDeConverter.CredentialId.class)
public class CredentialId extends Identifier {
    public static CredentialId of(UUID id) {
        CredentialId entityId = new CredentialId();
        entityId.setId(id);
        return entityId;
    }

    @Column(name = "credential_id")
    @Override
    public UUID getId() {
        return super.getId();
    }
}
