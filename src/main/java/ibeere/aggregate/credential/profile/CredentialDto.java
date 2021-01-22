package ibeere.aggregate.credential.profile;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import ibeere.aggregate.credential.Credential;
import ibeere.aggregate.credential.CredentialId;
import ibeere.aggregate.credential.CredentialType;

@Getter
public class CredentialDto {
    private final CredentialId credentialId;
    private final String text;
    private final CredentialType type;

    public CredentialDto(Credential credential) {
        this.credentialId = credential.getCredentialId();
        this.text = credential.getText();
        this.type = credential.getType();
    }

    public String getHumanText() {
        return type.getToHumanTextMapper().apply(text);
    }

    public boolean isEmpty() {
        return StringUtils.isBlank(text);
    }
}
