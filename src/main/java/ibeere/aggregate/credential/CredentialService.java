package ibeere.aggregate.credential;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ibeere.aggregate.credential.profile.TemplateCredential;
import ibeere.aggregate.question.answer.AnswerId;
import ibeere.user.UserId;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
@RequiredArgsConstructor
public class CredentialService {

    private final CredentialRepository repository;

    public List<ImmutableCredential> findBy(Collection<CredentialId> credentialIds) {
        return repository.findAllById(credentialIds).stream()
                .map(entity -> new ImmutableCredential(entity.getCredentialId(), entity.getType(), entity.getText(), entity.getAnswerId()))
                .collect(toList());
    }

    /**
     * Create new credentials given the templates supplied
     */
    public List<CredentialId> addFromTemplates(UserId userId, AnswerId answerId, List<TemplateCredential> templates) {
        return templates.stream()
                .map(template -> {
                    CredentialEntity credentialEntity = new CredentialEntity(
                            CredentialId.of(UUID.randomUUID()),
                            answerId,
                            userId,
                            template.getType(),
                            template.getText());
                    repository.save(credentialEntity);

                    return credentialEntity.getCredentialId();
                })
                .collect(toList());
    }
}
