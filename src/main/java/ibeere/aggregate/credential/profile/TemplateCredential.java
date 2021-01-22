package ibeere.aggregate.credential.profile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ibeere.aggregate.credential.Credential;
import ibeere.aggregate.credential.CredentialId;
import ibeere.aggregate.credential.CredentialType;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * @see TemplateCredentialEntity
 */
@RequiredArgsConstructor
@Getter
public class TemplateCredential implements Credential {
    protected final CredentialId credentialId;
    protected final CredentialType type;
    protected final String text;

    /** Can template be used for creating credentials given its state */
    public boolean isApplicable() {
        return type.getApplicabilityPredicate().test(text) && !isBlank(text);
    }

    public String mapToHumanText() {
        return type.getToHumanTextMapper().apply(text);
    }
}
