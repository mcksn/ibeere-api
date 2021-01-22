package ibeere.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfiguration{

    @Bean
    @Primary
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("questionById", "microProfile", "credentialProfile");

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .recordStats());
        return cacheManager;
    }

    @Bean
    public CacheManager userCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("userById");

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .recordStats());

        return cacheManager;
    }
}