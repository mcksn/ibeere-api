package ibeere.page.relevantpage;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
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
import java.util.UUID;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static ibeere.aggregate.question.QuestionDto.toQuestionDtoWithNoAnswers;


@RequiredArgsConstructor
@Service
public class RelevantPageService {
    private static final Logger LOG = LoggerFactory.getLogger(RelevantPageService.class);

    private final UserDocumentService userDocumentService;
    private final QuestionService questionService;
    private final QuestionDocumentService questionDocumentService;

    public ItemsPage relevantPages(String path, @Nullable UserId userId) {
        LOG.info("{} relevant page loaded", path);
        final Optional<User> user = ofNullable(userId).map(userDocumentService::get);

        List<QuestionId> relevantQuestionIds = questionService.findRelevantQuestion(path).stream()
                .map(RelevantQuestion::getQuestionId)
                .map(UUID::fromString)
                .map(QuestionId::of)
                .collect(toList());

        final Collection<Question> questions = questionDocumentService.declarePublishedQuestionStream(relevantQuestionIds).distinct().collect(toList());

        return new ItemsPage(questions.stream()
                .map(question -> new ItemsPage.Item(ItemType.QUESTION,
                        user.map(u -> toQuestionDtoWithNoAnswers(question, u))
                                .orElseGet(() -> toQuestionDtoWithNoAnswers(question))))
                .collect(toList()),
                false);
    }
}
