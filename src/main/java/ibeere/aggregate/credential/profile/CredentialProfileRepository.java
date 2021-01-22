package ibeere.aggregate.credential.profile;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ibeere.repository.CachingJpaRepository;
import ibeere.user.UserId;

import java.util.Optional;

public interface CredentialProfileRepository extends CachingJpaRepository<CredentialProfileEntity, UserId> {

    @Override
    Optional<CredentialProfileEntity> findById(UserId userId);

    @Modifying
    @Query(value = "update credential_entity set text = " +
            "(select COALESCE(sum(view_count), '0') from answer_entity where answer_entity.user_id = credential_entity.user_id)" +
            "where credential_entity.type = 'VIEWS'", nativeQuery = true)
    int updateViewsCredential();


    @Modifying
    @Query(value = "update credential_entity set text = " +
            "(select COALESCE(count(*), '0') from answer_entity where answer_entity.user_id = credential_entity.user_id)" +
            "where credential_entity.type = 'ANSWERED'", nativeQuery = true)
    int updateAnsweredCredential();
}