package ibeere.aggregate.profile.bio;

import lombok.Getter;
import ibeere.ddd.ImmutableEntity;
import ibeere.user.UserId;
import lombok.RequiredArgsConstructor;

/**
 * @see BioProfileEntity
 */
@Getter
@RequiredArgsConstructor
public class BioProfile implements ImmutableEntity {
    private final UserId userId;
    private final String bio;
}
