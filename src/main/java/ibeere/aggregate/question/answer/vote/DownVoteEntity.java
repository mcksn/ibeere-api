package ibeere.aggregate.question.answer.vote;

import ibeere.aggregate.question.answer.AnswerId;
import ibeere.user.UserId;
import lombok.Getter;

import javax.persistence.Id;
import java.time.Instant;
import java.util.UUID;

@javax.persistence.Entity
@Getter
public class DownVoteEntity implements ibeere.ddd.Entity {

    @Id
    private UUID id;
    private UserId userId;
    private AnswerId answerId;
    private Instant submitDate;

    public DownVoteEntity(UUID id,
                          UserId  userId,
                          AnswerId answerId,
                          Instant submitDate) {
        this.id = id;
        this.userId = userId;
        this.answerId = answerId;
        this.submitDate = submitDate;
    }

    // hibernate
    public DownVoteEntity() {
    }
}
