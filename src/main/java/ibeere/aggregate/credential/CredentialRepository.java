package ibeere.aggregate.credential;

import ibeere.repository.CachingJpaRepository;

public interface CredentialRepository extends CachingJpaRepository<CredentialEntity, CredentialId> {
}