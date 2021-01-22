package ibeere.aggregate.question;

import lombok.Getter;
import ibeere.page.common.LastUpdatedContent;
import ibeere.aggregate.question.answer.Answer;
import ibeere.aggregate.question.answer.AnswerDto;
import ibeere.aggregate.question.answer.AnswerId;
import ibeere.user.User;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static ibeere.aggregate.question.answer.AnswerDto.toAnswerDto;

@Getter
public class QuestionDto implements LastUpdatedContent, Cloneable {
    private final QuestionId questionId;
    private final String questionText;
    private final String path;
    private final String userName;
    private final long followingCount;
    private final boolean followed;
    private final Instant submitDate;
    private final List<AnswerDto> answers;
    private final Long answerCount;
    private final String url;
    private final Instant lastFollowed;
    private final String lastFollowedHumanReadable;

    private QuestionDto(Question question, User user, Map<AnswerId, Long> viewCountToAnswerIds) {
        this.questionId = question.getQuestionId();
        this.questionText = question.getQuestionText();
        this.path = question.getPath();
        this.userName = question.getUserName();
        this.followingCount = question.getFollowingCount();

        this.submitDate = question.getLastFollowed();
        this.answerCount = question.getAnswerCount();
        this.url = question.getUrl();
        this.lastFollowed = question.getLastFollowed();
        this.lastFollowedHumanReadable = question.getLastFollowedHumanReadable();

        this.followed = ofNullable(user).map(u -> u.isFollowing(question.getQuestionId())).orElse(false);
        this.answers = ofNullable(question.getAnswers()).map( as -> as.stream()
                .sorted(comparing(Answer::getUpVoteCounts).reversed())
                .map(a -> toAnswerDto(a,
                        user,
                        ofNullable(viewCountToAnswerIds).map(vcMap -> vcMap.get(a.getAnswerId())).orElse(0L)))
                .collect(toList())).orElse(EMPTY_LIST);
    }

    public static QuestionDto toQuestionDtoWithNoAnswers(Question question) {
        return new QuestionDto(question, null, null);
    }

    public static QuestionDto toQuestionDto(Question question, Map<AnswerId, Long> viewCountToAnswerId) {
        return new QuestionDto(question, null, viewCountToAnswerId);
    }

    public static QuestionDto toQuestionDto(Question question, User user, Map<AnswerId, Long> viewCountToAnswerId) {
        return new QuestionDto(question, user, viewCountToAnswerId);
    }

    public static QuestionDto toQuestionDtoWithNoAnswers(Question question, User user) {
        return new QuestionDto(question, user, null);
    }

    @Override
    public Instant getUpdated() {
        return submitDate;
    }

}
