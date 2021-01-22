package ibeere.aggregate.question.answer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ibeere.ddd.Identifier;
import ibeere.framework.IdentifierDeConverter;
import lombok.Setter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Access(AccessType.PROPERTY)
@Setter(PRIVATE)
@JsonDeserialize(converter = IdentifierDeConverter.AnswerId.class)
@Embeddable
public class AnswerId extends Identifier {
    public static AnswerId of(UUID id) {
        AnswerId answerId = new AnswerId();
        answerId.setId(id);
        return answerId;
    }

    @Column(name = "answer_id")
    @Override
    public UUID getId() {
        return super.getId();
    }

    public static AnswerId from(Identifier identifier) {
        AnswerId answerId = new AnswerId();
        answerId.setId(identifier.getId());
        return answerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnswerId that = (AnswerId) o;
        return Objects.equals(getId(), that.getId());
    }
}
