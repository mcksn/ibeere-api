package ibeere.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ibeere.repository.CachingJpaRepository;
import ibeere.user.auth.google.GoogleUserId;
import ibeere.user.auth.twitter.TwitterUserId;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CachingJpaRepository<UserEntity, UserId> {
    Optional<UserEntity> findById(UserId userId);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByTwitterUserId(TwitterUserId twitterUserId);
    Optional<UserEntity> findByGoogleUserId(GoogleUserId googleUserId);

    @Query("SELECT u.id FROM UserEntity u")
    List<UserId> findAllId();
}
