package ibeere.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CachingJpaRepository<T, ID> extends JpaRepository<T, ID> {

    // TODO implement some caching here
}