package ibeere.aggregate.profile.micro;

import org.springframework.data.jpa.repository.Query;
import ibeere.repository.CachingJpaRepository;
import ibeere.user.UserId;

import java.util.List;
import java.util.Optional;

public interface MicroProfileRepository extends CachingJpaRepository<MicroProfileEntity, UserId> {

    Optional<MicroProfileEntity> findByPath(String path);
    @Query("SELECT m.id FROM MicroProfileEntity m")
    List<UserId> findIdsAll();
    @Query("SELECT m.id FROM MicroProfileEntity m WHERE m.path = :path")
    Optional<UserId> findIdByPath(String path);
}