package ibeere.aggregate.credential;

import ibeere.aggregate.credential.profile.TemplateCredential;
import ibeere.aggregate.question.answer.AnswerId;

/**
 * @see CredentialEntity
 *
 * The fact that it extends TemplateCredential is just for convenience for shared attributes.
 * It is coincidental that they share the same attributes and may diverge in future.
 * Refactor needed when pros of convenience out way the cons of confusion.
 */
public class ImmutableCredential extends TemplateCredential {
    private final AnswerId answerId;

    public ImmutableCredential(CredentialId credentialId, CredentialType type, String text, AnswerId answerId) {
        super(credentialId, type, text);
        this.answerId = answerId;
    }

    public AnswerId getAnswerId() {
        return answerId;
    }
}
