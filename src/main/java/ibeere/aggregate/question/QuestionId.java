package ibeere.aggregate.question;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ibeere.ddd.Identifier;
import ibeere.framework.IdentifierDeConverter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;

@Access(AccessType.PROPERTY)
@JsonDeserialize(converter = IdentifierDeConverter.QuestionId.class)
@Embeddable
public class QuestionId extends Identifier {
    public static QuestionId of(UUID id) {
        QuestionId questionId = new QuestionId();
        questionId.setId(id);
        return questionId;
    }

    public static QuestionId of(UUID id, String note) {
        QuestionId questionId = new QuestionId();
        questionId.setId(id);
        questionId.setNote(note);
        return questionId;
    }

    @Column(name = "question_id")
    @Override
    public UUID getId() {
        return super.getId();
    }

    public static QuestionId from(Identifier identifier) {
        QuestionId questionId = new QuestionId();
        questionId.setId(identifier.getId());
        return questionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionId that = (QuestionId) o;
        return Objects.equals(getId(), that.getId());
    }
}
