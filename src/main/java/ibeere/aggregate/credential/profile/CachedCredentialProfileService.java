package ibeere.aggregate.credential.profile;

import ibeere.user.UserId;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("credentialProfileService")
public class CachedCredentialProfileService extends CredentialProfileService {

    private final CredentialProfileCache cache;

    public CachedCredentialProfileService(CredentialProfileRepository repository, CredentialProfileCache cache) {
        super(repository);
        this.cache = cache;
    }

    public void rebuild(UserId userId) {
        cache.rebuild(userId);
    }

    public Optional<CredentialProfile> findById(UserId userId) {
        return cache.get(userId);
    }
}
