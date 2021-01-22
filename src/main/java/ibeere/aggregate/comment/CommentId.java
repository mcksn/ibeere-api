package ibeere.aggregate.comment;

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
@Embeddable
@JsonDeserialize(converter = IdentifierDeConverter.CommentId.class)
public class CommentId extends Identifier {
    public static CommentId of(UUID id) {
        CommentId commentId = new CommentId();
        commentId.setId(id);
        return commentId;
    }

    @Column(name = "comment_id")
    @Override
    public UUID getId() {
        return super.getId();
    }

    public static CommentId from(Identifier identifier) {
        CommentId commentId = new CommentId();
        commentId.setId(identifier.getId());
        return commentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentId that = (CommentId) o;
        return Objects.equals(getId(), that.getId());
    }
}
