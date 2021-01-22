package ibeere.aggregate.profile;

import ibeere.ddd.RootEntity;
import ibeere.user.UserId;

import javax.persistence.EmbeddedId;
import javax.persistence.MappedSuperclass;

/**
 * TODO split table so micro, bio and credential have their own tables
 */
@MappedSuperclass
public class AbstractProfileEntity implements RootEntity<UserId> {

    @EmbeddedId
    private UserId id;

    public AbstractProfileEntity(UserId id) {
        this.id = id;
    }

    // hibernate
    public AbstractProfileEntity() {
    }

    @Override
    public UserId getId() {
        return id;
    }
}
