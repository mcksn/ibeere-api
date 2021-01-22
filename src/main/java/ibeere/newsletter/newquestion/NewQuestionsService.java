package ibeere.newsletter.newquestion;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ibeere.newsletter.newquestion.question.Question;
import ibeere.newsletter.newquestion.question.QuestionRepository;
import ibeere.questiondoc.QuestionDocumentService;
import ibeere.user.UserId;
import ibeere.user.UserService;

import java.util.*;

import static java.util.Optional.ofNullable;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
@Transactional
@RequiredArgsConstructor
public class NewQuestionsService {

    private static final Logger LOG = LoggerFactory.getLogger(NewQuestionsService.class);

    private final QuestionRepository repository;
    private final QuestionDocumentService questionDocumentService;
    private final NewQuestionsEmailGenerator newQuestionsEmailGenerator;
    private final UserService userService;

    public void sendEmails(UserId overrideUserId) {

        final List<Question> newsletterQuestionsToSend = repository.findBySentForNewQuestionsNot(true);
        final Set<ibeere.aggregate.question.Question> questions = newsletterQuestionsToSend.stream()
                .map(ques -> questionDocumentService.get(ques.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .limit(10)
                .collect(toSet());

        if (questions.size() < 3) {
            LOG.info("Not sending any emails. less than three questions to send");
            return;
        }

        Runnable setFlag = () -> {
            if (!ofNullable(overrideUserId).isPresent()) {
                repository.findAllById(questions.stream().map(ibeere.aggregate.question.Question::getQuestionId).collect(toList()))
                        .stream()
                        .peek(Question::sentOut)
                        .forEach(this.repository::save);
            }
        };

        supplyAsync(() -> {

            LOG.info(questions.size() + " questions to include in email");
            final Set<UserId> allUserIds = ofNullable(overrideUserId)
                    .map(Collections::singleton)
                    .orElseGet(() -> new HashSet<>(userService.findAllById()));
            final List<String> validEmailsByIds = userService.findValidEmailsByIds(allUserIds);

            return validEmailsByIds.stream()
                    .map(email -> newQuestionsEmailGenerator.generate(email, questions))
                    .mapToInt(i -> i ? 0 : 1) //so failed are counted
                    .sum();

        }).whenComplete((failedCount, throwable) -> {
            if (throwable != null) {
                LOG.error("Failed newsletter", throwable);
            }
            if (failedCount > 0) {
                LOG.info("Failed to send {} emails.", failedCount);
            }
            setFlag.run();
        });
    }
}

