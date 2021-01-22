package ibeere.aggregate.draftanswer;

import ibeere.aggregate.question.QuestionId;
import ibeere.audience.Audience;
import ibeere.ddd.Entity;
import ibeere.support.ClockProvider;
import ibeere.user.UserId;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Lob;
import java.time.Instant;
import java.util.UUID;

import static java.time.Instant.now;

/**
 * Draft of an answer that can be updated until it is converted into an answer.
 * Should in theory be content agnostic but there are some utility methods in other areas that assume a particular format
 */
@javax.persistence.Entity
@Getter
public class DraftAnswerEntity implements Entity<UUID> {
    @EmbeddedId
    private UUID id;
    private UserId userId;
    private String userName;
    private String userBio;
    private QuestionId questionId;
    @Lob
    private String editorState;
    private Instant submitDate;
    private Instant updated;
    private Audience audience;
    private String path;

    public DraftAnswerEntity(UUID id,
                             UserId userId,
                             String userName, String editorState,
                             QuestionId questionId,
                             Instant submitDate,
                             Instant updated,
                             String path,
                             String rejectionReason,
                             String userBio,
                             Audience audience) {
        this.id = id;
        this.userName = userName;
        this.userId = userId;
        this.editorState = editorState;
        this.questionId = questionId;
        this.submitDate = submitDate;
        this.updated = updated;
        this.path = path;
        this.userBio = userBio;
        this.audience = audience;
    }

    // hibernate
    public DraftAnswerEntity() {
    }

    public void updateEditorState(String newEditorState, Audience audience) {
        this.editorState = newEditorState;
        this.audience = audience;
        this.updated = now(ClockProvider.STANDARD_CLOCK);
    }
}
