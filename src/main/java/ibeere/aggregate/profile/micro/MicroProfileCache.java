package ibeere.aggregate.profile.micro;

import ibeere.framework.ImmutableEntityCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ibeere.user.UserId;

import java.util.Optional;

import static java.util.Optional.empty;

@Service
@CacheConfig(cacheNames = {"microProfile"})
public class MicroProfileCache implements ImmutableEntityCache<UserId, MicroProfile> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MicroProfileCache.class);

    private final MicroProfileService microProfileService;
    private final CacheManager cacheManager;

    public MicroProfileCache(@Qualifier("uncachedMicroProfileService") MicroProfileService microProfileService,
                             CacheManager cacheManager) {
        this.microProfileService = microProfileService;
        this.cacheManager = cacheManager;
    }

    @Override
    public Optional<MicroProfile> get(UserId userId) {
        LOGGER.info("bypassed cache. getting " + userId);
        return find(userId);
    }

    @Override
    public Optional<MicroProfile> rebuild(UserId userId) {
        LOGGER.info("rebuild" + userId);
        return find(userId);
    }

    private Optional<MicroProfile> find(UserId userId) {
        try {
            return this.microProfileService.findMicroBy(userId);
        } catch (Exception e) {
            LOGGER.error(userId.toString(), e);
            return empty();
        }
    }
}
