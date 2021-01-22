package ibeere.newsletter.topanswers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ibeere.newsletter.newquestion.question.QuestionRepository;
import ibeere.aggregate.question.answer.Answer;
import ibeere.aggregate.question.answer.AnswerQuestionRef;
import ibeere.questiondoc.QuestionDocumentService;
import ibeere.user.UserId;
import ibeere.user.UserService;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.toSet;

@Service
@Transactional
@RequiredArgsConstructor
public class TopAnswersService {

    private static final Logger LOG = LoggerFactory.getLogger(TopAnswersService.class);

    private final QuestionRepository repository;
    private final QuestionDocumentService questionDocumentService;
    private final TopAnswersEmailGenerator emailGenerator;
    private final UserService userService;

    public void sendTopAnswers(Set<AnswerQuestionRef> answerQuestionRefs, UserId overrideUserId) {

        final Set<Answer> answers = answerQuestionRefs.stream()
                .map(answerQuestionRef -> questionDocumentService.get(answerQuestionRef.questionId())
                        .flatMap(q -> q.getAnswers().stream()
                                .filter(answer -> answer.getAnswerId().equals(answerQuestionRef.answerId()))
                                .findAny())
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(toSet());

        if (answers.size() < 2) {
            LOG.info("Not sending any emails. Less than two answers to send");
            return;
        }

        supplyAsync(() -> {

            LOG.info(answers.size() + " questions to include in email");
            final Set<UserId> allUserIds = ofNullable(overrideUserId)
                    .map(Collections::singleton)
                    .orElseGet(() -> newHashSet(userService.findAllById()));

            return userService.findValidEmailsByIds(allUserIds).stream()
                    .map(email -> emailGenerator.generate(email, answers))
                    .mapToInt(f -> f ? 0 : 1) //so failed are counted
                    .sum();
        })
                .whenComplete((failedCount, throwable) -> {
                    if (throwable != null) {
                        LOG.error("Failed newsletter", throwable);
                    }
                    if (failedCount > 0) {
                        LOG.info("Failed to send {} emails.", failedCount);
                    }
                });
    }
}

