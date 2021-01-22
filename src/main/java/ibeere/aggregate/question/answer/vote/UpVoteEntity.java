package ibeere.aggregate.question.answer.vote;

import lombok.Getter;
import ibeere.aggregate.question.answer.AnswerId;
import ibeere.user.UserId;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@javax.persistence.Entity
@Getter
public class UpVoteEntity implements ibeere.ddd.Entity {
    @EmbeddedId
    private UUID id;
    private UserId userId;
    private AnswerId answerId;
    private Instant submitDate;

    public UpVoteEntity(UUID id,
                        UserId userId,
                        AnswerId answerId,
                        Instant submitDate) {
        this.id = id;
        this.userId = userId;
        this.answerId = answerId;
        this.submitDate = submitDate;
    }

    // hibernate
    public UpVoteEntity() {
    }
}
