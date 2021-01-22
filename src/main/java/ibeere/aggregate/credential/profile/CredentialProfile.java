package ibeere.aggregate.credential.profile;

import ibeere.aggregate.credential.CredentialId;
import ibeere.ddd.ImmutableEntity;
import ibeere.user.UserId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @see CredentialProfileEntity
 */
@RequiredArgsConstructor
public class CredentialProfile implements ImmutableEntity {
    @Getter
    private final UserId userId;
    private final List<TemplateCredential> credentials;

    public List<TemplateCredential> getCredentialOptionsForAnAnswer() {
        return credentials.stream()
                .filter(TemplateCredential::isApplicable)
                .collect(Collectors.toList());
    }

    public List<TemplateCredential> getCredentials(Set<CredentialId> withCredentialIds, boolean userGenerated) {
        return credentials.stream()
                .filter(cred -> withCredentialIds.contains(cred.getCredentialId()))
                .filter(cred -> cred.type.isUserGenerated() == userGenerated)
                .collect(Collectors.toList());
    }

    public List<TemplateCredential> getCredentials(Set<CredentialId> withCredentialIds) {
        return credentials.stream()
                .filter(cred -> withCredentialIds.contains(cred.getCredentialId()))
                .collect(Collectors.toList());
    }
}
