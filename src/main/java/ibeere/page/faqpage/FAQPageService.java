package ibeere.page.faqpage;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ibeere.meta.MetaQuestionsService;
import ibeere.page.common.ItemType;
import ibeere.page.common.ItemsPage;
import ibeere.aggregate.question.*;
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
public class FAQPageService {
    private static final Logger LOG = LoggerFactory.getLogger(FAQPageService.class);

    private final MetaQuestionsService metaQuestionsService;
    private final UserDocumentService userDocumentService;
    private final QuestionDocumentService questionDocumentService;

    public ItemsPage findFAQPage(@Nullable UserId userId) {
        LOG.info("faq page loaded");
        final Optional<User> user = ofNullable(userId).map(userDocumentService::get);

        List<QuestionId> questionIds = metaQuestionsService.fetchContentPageMetaQuestionIds();

        final Collection<Question> questions = questionDocumentService.declarePublishedQuestionStream(questionIds).distinct().collect(toList());

        return new ItemsPage(questions.stream().map(Question::getFirstAnswer)
                .map(firstAnswer -> new ItemsPage.Item(ItemType.ANSWER,
                        user.map(u -> toAnswerDto(firstAnswer, u, firstAnswer.getViewCount())).orElseGet(() -> toAnswerDto(firstAnswer, firstAnswer.getViewCount()))))
                .collect(toList()),
                false);
    }
}
