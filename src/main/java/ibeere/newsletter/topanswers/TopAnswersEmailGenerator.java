package ibeere.newsletter.topanswers;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ibeere.mail.EmailService;
import ibeere.mail.Mail;
import ibeere.aggregate.question.answer.Answer;
import ibeere.aggregate.question.answer.AnswerQuestionRef;
import ibeere.aggregate.question.answer.Image;

import java.util.*;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Service
class TopAnswersEmailGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(TopAnswersEmailGenerator.class);
    private static final String SUBJECT = "Weekly Top Answers";

    private final String appHost;
    private final EmailService emailService;
    private final EmailTextVersionGenerator generator;

    public TopAnswersEmailGenerator(@Value("${app.host}") String appHost,
                                    EmailService emailService,
                                    EmailTextVersionGenerator generator) {
        this.appHost = appHost;
        this.emailService = emailService;
        this.generator = generator;
    }

    public boolean generate(String email, Set<Answer> answers) {
        final List<AnswerQuestionRef> answerQuestionRefs = answers.stream().map(Answer::answerQuestionRef).collect(toList());
        LOGGER.info("Sending email to {} for {}", email, answerQuestionRefs);

        final List<AnswerModel> answerModels = answers.stream()
                .map(answer -> AnswerModel.builder()
                        .profileUrl(answer.getProfile().getUrl())
                        .profileImgUrl(answer.getProfile().getImgUrl())
                        .content(answer.microContentPreview())
                        .credentials(ofNullable(answer.getProfile().getCredentials()).orElse(""))
                        .profileName(answer.getProfile().getName())
                        .questionText(answer.getQuestion().getQuestionText())
                        .date(answer.getAnsweredActualHumanDate())
                        .date(appHost)
                        .questionUrl(answer.getQuestion().getUrl())
                        .answerTag(answer.getTag())
                        .answerImageUrl(ofNullable(answer.getFirstImage()).map(Image::getUri).orElse(null))
                        .build())
                .collect(toList());

        final Mail mail = Mail.builder()
                .from("team@ibeere.com")
                .to(email)
                .subject(SUBJECT)
                .bulk(true)
                .model(ImmutableMap
                        .of("email", email,
                                "location", "Nigeria",
                                "signature", "https://ibeere.com",
                                "answers", answerModels,
                                "host", appHost))
                .textVersion(generator.generate(SUBJECT, answerModels))
                .build();

        try {
            if (!appHost.contains("localhost")) {
                emailService.sendSimpleMessage(mail, "top.answers.ftlh");
                return true;
            } else {
                LOGGER.info("Not sending email to {} for {} on account of appHost is {}", email, answerQuestionRefs, appHost);
            }
        } catch (Exception e) {
            LOGGER.error("generate()", e);
        }

        return false;
    }
}
