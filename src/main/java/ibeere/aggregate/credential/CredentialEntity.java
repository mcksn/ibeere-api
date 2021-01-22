package ibeere.aggregate.credential;

import org.hibernate.annotations.Where;
import ibeere.aggregate.question.answer.AnswerId;
import ibeere.user.UserId;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Credential of a user created for an answer.
 * Like a stamp on an answer created from a template.
 * Credentials shouldnt really change once they've been created.
 */
@Entity
@Table(name ="credential_entity")
@Where(clause = "answer_id is not null")
public class CredentialEntity extends AbstractCredentialEntity {
    @Embedded
    private AnswerId answerId;

    //hibernate
    public CredentialEntity() {
    }

    public CredentialEntity(CredentialId credentialId, AnswerId answerId, UserId userId, CredentialType credentialType, String text) {
        super(credentialId, userId, credentialType, text);
        this.answerId = answerId;
    }

    public AnswerId getAnswerId() {
        return answerId;
    }
}
