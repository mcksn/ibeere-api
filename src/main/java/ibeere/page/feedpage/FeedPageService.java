package ibeere.page.feedpage;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ibeere.page.common.ItemType;
import ibeere.page.common.ItemsPage;
import ibeere.aggregate.question.*;
import ibeere.aggregate.question.answer.AnswerQuestionRef;
import ibeere.aggregate.question.answer.AnswerViewCountService;
import ibeere.questiondoc.QuestionDocumentPopulater;
import ibeere.questiondoc.QuestionDocumentService;
import ibeere.user.User;
import ibeere.user.UserDocumentService;
import ibeere.user.UserId;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static ibeere.aggregate.question.answer.AnswerDto.toAnswerDto;
import static ibeere.aggregate.question.QuestionDto.toQuestionDtoWithNoAnswers;


@RequiredArgsConstructor
@Service
public class FeedPageService {
    private static final Logger LOG = LoggerFactory.getLogger(FeedPageService.class);

    private final QuestionDocumentPopulater questionDocumentPopulater;
    private final UserDocumentService userDocumentService;
    private final QuestionService questionService;
    private final QuestionDocumentService questionDocumentService;
    private final AnswerViewCountService answerViewCountService;

    public ItemsPage findFeedPage(@Nullable UserId userId, int pageNo) {
        LOG.info("{} feed page loaded", pageNo);
        final Optional<User> user = ofNullable(userId).map(userDocumentService::get);

        final QuestionId recentQuestionId = user.map(User::getRecentQuestionId).orElse(null);

        List<QuestionId> questionIds = questionDocumentPopulater.getPage(pageNo);

        if (recentQuestionId != null && pageNo == 1) {
            questionIds.add(0, recentQuestionId);
        }

        final Collection<Question> questions = questionDocumentService.declarePublishedQuestionStream(questionIds)
                .distinct()
                .collect(toList());

        return new ItemsPage(questions.stream().map(question -> {
            if (!question.hasAnswers()) {
                return new ItemsPage.Item(ItemType.QUESTION,
                        user.map(u -> toQuestionDtoWithNoAnswers(question, u))
                                .orElseGet(() -> toQuestionDtoWithNoAnswers(question)));
            } else {
                final Long viewCount = answerViewCountService.incAndShowViewCount(AnswerQuestionRef.of(question.getFirstAnswer().getAnswerId(),
                        question.getQuestionId()));
                return new ItemsPage.Item(ItemType.ANSWER,
                        user.map(u -> toAnswerDto(question.getFirstAnswer(), u, viewCount))
                                .orElseGet(() -> toAnswerDto(question.getFirstAnswer(), viewCount)));
            }
        })
                .collect(toList()),
                questionDocumentPopulater.isThereAPageAfter(pageNo));
    }

    public int countFeedReadyPercentage() {
        return (questionDocumentService.getCacheAsMap().keySet().size() /questionService.count()) * 100;
    }
}
