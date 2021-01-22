package ibeere.aggregate.question.answer;

import lombok.Getter;
import ibeere.aggregate.question.QuestionDto;
import ibeere.user.User;

import java.time.Instant;

import static java.util.Optional.ofNullable;
import static ibeere.aggregate.question.QuestionDto.toQuestionDtoWithNoAnswers;

@Getter
public class AnswerDto implements Cloneable {
    private final String contentPreview;
    private final Image firstImage;
    private final AnswerId answerId;
    private final QuestionDto question;
    private final String editorState;
    private final Instant submitDate;
    private final String answeredHumanDate;
    private final long upVoteCounts;
    private final String path;
    private final boolean upVoted;
    private final boolean downVoted;
    private final boolean upVotedByAsker;
    private final boolean downVotedByAsker;
    private final String answerText;
    private final String viewCount;
    private final long commentCount;
    private final boolean editable;
    private final String tag;
    private final AnswerProfileDto profile;
    private final boolean removed;

    private AnswerDto(Answer answer, User user, long viewCount) {
        this.answerId = answer.getAnswerId();
        this.answeredHumanDate = answer.getAnsweredHumanDate();
        this.downVoted = ofNullable(user).map(u -> u.hasDownVoted(answer.getAnswerId())).orElse(false);
        this.upVoted = ofNullable(user).map(u -> u.hasUpVoted(answer.getAnswerId())).orElse(false);
        this.upVotedByAsker = answer.isUpVotedByAsker();
        this.downVotedByAsker = answer.isDownVotedByAsker();
        this.editorState = answer.getEditorState();
        this.answerText = answer.getAnswerText();
        this.path = answer.getPath();
        this.question = toQuestionDtoWithNoAnswers(answer.getQuestion(), user);
        this.viewCount = answer.getHumanViewCount(viewCount);
        this.commentCount = answer.getCommentCount();
        this.submitDate = answer.getSubmitDate();
        this.upVoteCounts = answer.getUpVoteCounts();
        this.firstImage = answer.getFirstImage();
        this.contentPreview  = answer.getContentPreview();
        this.editable = answer.isEditable();
        this.tag = answer.getTag();
        this.profile = new AnswerProfileDto(answer.getProfile());
        this.removed = answer.removed();
    }

    public static AnswerDto toAnswerDto(Answer answer, User user, long viewCount) {
        return new AnswerDto(answer,
                user,
                viewCount);
    }

    public static AnswerDto toAnswerDto(Answer answer, long viewCount) {
        return new AnswerDto(answer, null, viewCount);
    }
}
