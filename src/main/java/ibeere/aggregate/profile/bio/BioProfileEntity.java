package ibeere.aggregate.profile.bio;

import lombok.Getter;
import ibeere.aggregate.profile.AbstractProfileEntity;
import ibeere.user.UserId;

import javax.persistence.*;

/** Profile in a bio context */
@Entity
@Getter
@Table(name = "profile_entity")
public class BioProfileEntity extends AbstractProfileEntity {

    @Lob
    private String bio;

    public BioProfileEntity(UserId id, String bio) {
        super(id);
        this.bio = bio;
    }

    // hibernate
    public BioProfileEntity() {
    }

    public void updateBio(String bio) {
        this.bio = bio;
    }
}
