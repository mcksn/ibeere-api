package ibeere.framework;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.util.Optional;

/**
 * Cache for immutable entities. Powered by uncached services. Improves performance.
 * Using this approach opposed to at the repository layer for now
 * to gain the benefits of whole aggregates cached rather than just individual entities.
 */
public interface ImmutableEntityCache<I, E> {

    @Cacheable
    Optional<E> get(I id);

    @CachePut
    Optional<E> rebuild(I id);

}
