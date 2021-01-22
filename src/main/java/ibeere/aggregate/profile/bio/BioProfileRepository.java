package ibeere.aggregate.profile.bio;

import ibeere.repository.CachingJpaRepository;
import ibeere.user.UserId;

public interface BioProfileRepository extends CachingJpaRepository<BioProfileEntity, UserId> {
}