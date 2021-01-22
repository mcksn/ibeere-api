package ibeere.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ibeere.ddd.Identifier;
import ibeere.framework.IdentifierDeConverter;
import lombok.EqualsAndHashCode;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;

@Access(AccessType.PROPERTY)
@Embeddable
@EqualsAndHashCode
@JsonDeserialize(converter = IdentifierDeConverter.UserId.class)
public class UserId extends Identifier {
    public static UserId of(UUID id) {
        UserId entityId = new UserId();
        entityId.setId(id);
        return entityId;
    }

    @Column(name = "user_id")
    @Override
    public UUID getId() {
        return super.getId();
    }

}
