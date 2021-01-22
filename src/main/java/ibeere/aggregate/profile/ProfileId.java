package ibeere.aggregate.profile;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ibeere.ddd.Identifier;
import ibeere.framework.IdentifierDeConverter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;

@Access(AccessType.PROPERTY)
@Embeddable
@JsonDeserialize(converter = IdentifierDeConverter.ProfileId.class)
public class ProfileId extends Identifier {
    public static ProfileId of(UUID id) {
        ProfileId profileId = new ProfileId();
        profileId.setId(id);
        return profileId;
    }

    @Column(name = "profile_id")
    @Override
    public UUID getId() {
        return super.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfileId that = (ProfileId) o;
        return Objects.equals(getId(), that.getId());
    }
}
