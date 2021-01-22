package ibeere.aggregate.comment;

import lombok.Getter;
import ibeere.aggregate.comment.profile.CommentProfileDto;
import ibeere.ddd.ImmutableEntity;
import ibeere.aggregate.question.answer.AnswerId;
import ibeere.aggregate.question.QuestionId;
import ibeere.user.UserId;

import java.time.Instant;

@Getter
public class CommentDto implements Cloneable, ImmutableEntity {
    private final CommentId commentId;
    private final AnswerId answerId;
    private final QuestionId questionId;
    private final CommentProfileDto profile;
    private final String content;
    private final Instant submitDate;
    private final String shortDate;
    private final boolean deletable;

    public CommentDto(Comment comment, UserId requesterId) {
        this.commentId = comment.getCommentId();
        this.answerId = comment.getAnswerId();
        this.questionId = comment.getQuestionId();
        this.profile = new CommentProfileDto(comment.getProfile());
        this.content = comment.getContent();
        this.submitDate = comment.getSubmitDate();
        this.shortDate = comment.getShortDate();
        this.deletable = comment.getProfile().getUserId().equals(requesterId);
    }
}
