package ibeere.aggregate.profile.micro;

import ibeere.user.UserId;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service("microProfileService")
public class CachedMicroProfileService extends MicroProfileService {

    private final MicroProfileCache cache;

    public CachedMicroProfileService(MicroProfileRepository repository, MicroProfileCache cache) {
        super(repository);
        this.cache = cache;
    }

    public void rebuild(UserId userId) {
        cache.rebuild(userId);
    }

    public Optional<MicroProfile> findMicroBy(UserId userId) {
        return cache.get(userId);
    }

    public List<MicroProfile> findMicroBy(Set<UserId> userIds) {
        return userIds.stream().map(this::findMicroBy)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MicroProfile> findAllMicro() {
        return findMicroBy(new HashSet<>(repository.findIdsAll()));
    }

    @Transactional(readOnly = true)
    public Optional<MicroProfile> findMicroByPath(String path) {
        return repository.findIdByPath(path).flatMap(this::findMicroBy);
    }
}
