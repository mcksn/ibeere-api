package ibeere.aggregate.credential.profile;

import ibeere.framework.ImmutableEntityCache;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ibeere.user.UserId;

import java.util.Optional;

import static java.util.Optional.empty;

@Service
@CacheConfig(cacheNames = {"credentialProfile"})
@RequiredArgsConstructor
public class CredentialProfileCache implements ImmutableEntityCache<UserId, CredentialProfile> {
    private static final Logger LOG = LoggerFactory.getLogger(CredentialProfileCache.class);

    private final CredentialProfileService uncachedCredentialProfileService;

    @Override
    public Optional<CredentialProfile> get(UserId userId) {
        LOG.info("bypassed cache. getting " + userId);
        return find(userId);
    }

    @Override
    public Optional<CredentialProfile> rebuild(UserId userId) {
        LOG.info("rebuild" + userId);
        return find(userId);
    }

    private Optional<CredentialProfile> find(UserId userId) {
        try {
            return this.uncachedCredentialProfileService.findById(userId);
        } catch (Exception e) {
            LOG.error(userId.toString(), e);
            return empty();
        }
    }
}
