package ibeere.newsletter.answered;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.CompletableToListenableFutureAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import ibeere.aggregate.question.QuestionService;
import ibeere.aggregate.question.answer.Answer;
import ibeere.aggregate.question.answer.AnswerQuestionRef;
import ibeere.aggregate.question.Question;
import ibeere.questiondoc.QuestionDocumentService;
import ibeere.user.UserId;
import ibeere.user.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static ibeere.newsletter.answered.EmailInterestType.ASKED;
import static ibeere.newsletter.answered.EmailInterestType.FOLLOWED;

@Service
@RequiredArgsConstructor
public class QuestionAnsweredRebuiltConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(QuestionAnsweredRebuiltConsumer.class);

    private final QuestionDocumentService questionDocumentService;
    private final QuestionAnsweredEmailGenerator questionAnsweredEmailGenerator;
    private final QuestionService questionService;
    private final UserService userService;

    @ServiceActivator(inputChannel = "questionAnsweredRebuiltChannel", outputChannel = "ackChannel", async = "true")
    public ListenableFuture<Message> messageReceiver(Message message) {

        LOG.info("event consumed " + message.getPayload());

        return new CompletableToListenableFutureAdapter(CompletableFuture.supplyAsync(() -> {

            // TODO move into a service

            AnswerQuestionRef answerQuestionRef = new Gson().fromJson(message.getPayload().toString(), AnswerQuestionRef.class);

            final Optional<Question> question = questionDocumentService.get(answerQuestionRef.questionId());

            question
                    .flatMap(aQuestion -> aQuestion.answerById(answerQuestionRef.answerId()))
                    .ifPresent(anAnswer -> {
                        final List<UserId> followers = questionService.findFollowersFor(anAnswer.getQuestion().getQuestionId())
                                .stream().filter(followerUserId -> !followerUserId.equals(anAnswer.getProfile().getUserId()))
                                .collect(toList());

                        final Set<String> emailsByIds = userService.findValidEmailsByIds(followers).stream()
                                .collect(toSet());

                        emailsByIds.forEach(email -> send(message, anAnswer, email, FOLLOWED));

                        userService.findValidEmailsByIds(singleton(anAnswer.getQuestion().getUserId())).stream()
                                .findAny()
                                .ifPresent(email -> send(message, anAnswer, email, ASKED));

                    });

            return message;
        }));
    }

    private void send(Message message, Answer anAnswer, String email, EmailInterestType interest) {
        try {
            questionAnsweredEmailGenerator.generate(email, anAnswer, interest);
            userService.recordEmailSent(email);
        } catch (Exception e) {
            LOG.error("messageReceiver {} {}", message.getPayload(), e);
        }
    }
}
