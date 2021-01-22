package ibeere.page.contentpage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ibeere.aggregate.draftanswer.DraftAnswer;
import ibeere.aggregate.draftanswer.DraftAnswerService;
import ibeere.aggregate.draftanswer.DraftDto;
import ibeere.framework.AggregatesNotSyncedException;
import ibeere.meta.MetaQuestionsService;
import ibeere.page.common.LastUpdatedContent;
import ibeere.aggregate.question.*;
import ibeere.aggregate.question.answer.Answer;
import ibeere.aggregate.question.answer.AnswerQuestionRef;
import ibeere.aggregate.question.answer.AnswerService;
import ibeere.aggregate.question.answer.AnswerViewCountService;
import ibeere.questiondoc.QuestionDocumentService;
import ibeere.user.User;
import ibeere.user.UserDocumentService;
import ibeere.user.UserId;
import ibeere.user.UserService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static ibeere.page.contentpage.ContentType.*;
import static ibeere.aggregate.question.answer.AnswerDto.toAnswerDto;
import static ibeere.aggregate.question.QuestionDto.toQuestionDtoWithNoAnswers;


@RequiredArgsConstructor
@Service
public class ContentPageService {

    private final UserDocumentService userDocumentService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final DraftAnswerService draftAnswerService;
    private final UserService userService;
    private final QuestionDocumentService questionDocumentService;
    private final MetaQuestionsService metaQuestionsService;
    private final AnswerViewCountService answerViewCountService;

    public ContentPage getContentPage(UserId userId) {

        final User user = userDocumentService.get(userId);

        final List<QuestionId> publishedQues = questionService.findIdByUserId(userId);
        final List<AnswerQuestionRef> publishedAnswers = answerService.findPublishedAnswersByDeviceId(user.getId());
        final List<DraftAnswer> draftAnswers = draftAnswerService.findDraftAnswers(user.getId());

        final List<QuestionId> questionIdsFollowed = questionService.findQuestionsFollowed(user.getId());

        final List<LastUpdatedContent> contentList = concat(
                concat(questionDocumentService.declareAllPublishAnswerStream(publishedAnswers),
                        questionDocumentService.declarePublishedQuestionStream(publishedQues)),
                concat(draftAnswers.stream(),
                        questionDocumentService.declareQuestionFollowedStream(questionIdsFollowed)))
                .collect(toList());

        final List<ContentPage.ContentItem> contentItems =
                contentList.stream()
                        .sorted(Comparator.comparing(LastUpdatedContent::getUpdated))
                        .map(c -> {
                            if (c instanceof Question) {
                                final QuestionDto questionDto = toQuestionDtoWithNoAnswers((Question) c, user);
                                if  (questionDto.isFollowed()) {
                                    return  new ContentPage.ContentItem(FOLLOWED_QUESTION, questionDto);
                                } else if (metaQuestionsService.fetchContentPageMetaQuestionIds().contains(questionDto.getQuestionId())) {
                                    return new ContentPage.ContentItem(HELP, questionDto);
                                } else {
                                    return new ContentPage.ContentItem(PUBLISHED_QUESTION, questionDto);
                                }
                            } else if (c instanceof DraftAnswer) {
                                final Optional<Question> question = questionDocumentService.get(((DraftAnswer) c).getQuestionId());
                                return new ContentPage.ContentItem(DRAFT_ANSWER,
                                        new DraftDto((DraftAnswer) c, question.orElseThrow(AggregatesNotSyncedException::new)));
                            } else {
                                return new ContentPage.ContentItem(PUBLISHED_ANSWER, toAnswerDto((Answer) c, user,
                                        this.answerViewCountService.showViewCount(AnswerQuestionRef.of(((Answer) c).getAnswerId(),
                                                ((Answer) c).getQuestion().getQuestionId()))));
                            }
                        })
                        .collect(toList());
        return new ContentPage(contentItems);
    }
}
