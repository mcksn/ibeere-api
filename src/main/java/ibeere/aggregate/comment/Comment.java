package ibeere.aggregate.comment;

import lombok.Getter;
import ibeere.support.Utils;
import ibeere.aggregate.comment.profile.CommentProfile;
import ibeere.aggregate.profile.micro.MicroProfile;
import ibeere.aggregate.question.answer.AnswerId;
import ibeere.aggregate.question.QuestionId;

import java.time.Instant;

/**
 * @see CommentEntity
 */
@Getter
public class Comment {
    private final CommentId commentId;
    private final AnswerId answerId;
    private final QuestionId questionId;
    private final CommentProfile profile;
    private final String content;
    private final Instant submitDate;

    public Comment(CommentId commentId, AnswerId answerId, QuestionId questionId, MicroProfile profile, String content, Instant submitDate) {
        this.commentId = commentId;
        this.answerId = answerId;
        this.questionId = questionId;
        this.profile = new CommentProfile(profile);
        this.content = content;
        this.submitDate = submitDate;
    }
    public String getShortDate() {
        return Utils.getShortDateToHuman(submitDate).orElse(null);
    }
}