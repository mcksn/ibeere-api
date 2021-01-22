package ibeere.aggregate.credential.profile;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ibeere.aggregate.credential.CredentialId;
import ibeere.aggregate.credential.CredentialType;
import ibeere.user.auth.TemporaryUser;
import ibeere.user.UserId;
import ibeere.user.UserInputException;

import java.util.Optional;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service("uncachedCredentialProfileService")
@RequiredArgsConstructor
public class CredentialProfileService {
    private static final Logger LOG = LoggerFactory.getLogger(CredentialProfileService.class);

    protected final CredentialProfileRepository repository;

    @Transactional(readOnly = true)
    public Optional<CredentialProfile> findById(UserId userId) {
        return repository.findById(userId)
                .map(entity -> new CredentialProfile(entity.getId(), entity.getCredentials().stream()
                        .map(CredentialProfileService::map)
                        .collect(toList())));
    }

    @Transactional(propagation = REQUIRES_NEW)
    public void newProfile(TemporaryUser user) {

        if (repository.existsById(user.getUserId())) {
            LOG.warn("Attempt to add profile {} when already added", user.getUserId());
        }
        CredentialProfileEntity entity = repository.findById(user.getUserId()).get();

        entity.getCredentials().add(new TemplateCredentialEntity(CredentialId.of(randomUUID()), user.getUserId(), CredentialType.VIEWS, CredentialType.VIEWS.getDefaultText()));
        entity.getCredentials().add(new TemplateCredentialEntity(CredentialId.of(randomUUID()), user.getUserId(), CredentialType.ANSWERED, CredentialType.ANSWERED.getDefaultText()));

        repository.save(entity);
    }


    public static TemplateCredential map(TemplateCredentialEntity c) {
        return new TemplateCredential(c.getCredentialId(), c.getType(), c.getText());
    }

    @Transactional
    public void updateTemplateCredential(CredentialId credentialId, UserId requesterId, UserId userId, String text) throws UserInputException {

        if (isEmpty(text) || isEmpty(text.trim())) {
            return;
        }

        if (text.length() > 255) {
            throw new UserInputException("Text is too large.");
        }

        repository.findById(userId).ifPresent(profile -> profile
                .updateUserGenCredentialIfPresent(credentialId, text));
    }

    @Transactional
    public void addTemplateCredential(CredentialType credentialType, UserId requesterId, UserId userId, String text) throws UserInputException {

        if (isEmpty(text) || isEmpty(text.trim())) {
            return;
        }

        if (text.length() > 255) {
            throw new UserInputException("Text is too large.");
        }

        repository.findById(userId)
                .ifPresent(profile -> profile.addUserGenCredentialIfNoClash(new TemplateCredentialEntity(CredentialId.of(randomUUID()), userId, credentialType, text)));

    }

    @Transactional
    public void removeTemplateCredential(CredentialId credentialId, UserId requesterId, UserId userId) {

        repository.findById(userId).ifPresent(profile -> profile
                .removeUserGenCredentialIfPresent(credentialId));
    }

    @Transactional
    @Scheduled(cron = "0 0 20 * * ?")
    public boolean updateViewsCredential() {
        final int count = repository.updateViewsCredential();
        LOG.info("Updated {} {} credential counts", count, CredentialType.VIEWS);
        return true;
    }

    @Transactional
    @Scheduled(cron = "0 0 21 * * ?")
    public boolean updateAnsweredCredential() {
        final int count = repository.updateAnsweredCredential();
        LOG.info("Updated {} {} credential counts", count, CredentialType.ANSWERED);
        return true;
    }
}
