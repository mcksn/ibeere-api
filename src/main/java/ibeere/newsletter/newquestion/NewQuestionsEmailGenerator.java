package ibeere.newsletter.newquestion;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ibeere.mail.EmailService;
import ibeere.mail.Mail;
import ibeere.aggregate.question.QuestionId;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Service
class NewQuestionsEmailGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewQuestionsEmailGenerator.class);
    private static final String SUBJECT = "Weekly Top Answers";

    private final String appHost;
    private final EmailService emailService;

    public NewQuestionsEmailGenerator(@Value("${app.host}") String appHost, EmailService emailService) {
        this.appHost = appHost;
        this.emailService = emailService;
    }

    public boolean generate(String email, Set<ibeere.aggregate.question.Question> questions) {
        final List<QuestionId> questionIds = questions.stream().map(ibeere.aggregate.question.Question::getQuestionId).collect(toList());
        LOGGER.info("Sending email to {} for {}", email, questionIds);

        final List<QuestionModel> questionModels = questions.stream()
                .map(question -> new QuestionModel(question.getQuestionText(), question.getUrl()))
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
                                "questions", questionModels,
                                "host", appHost))
                .build();

        try {
            if (!appHost.contains("localhost")) {
                emailService.sendSimpleMessage(mail, "newsletter.new.questions.ftlh");
                return true;
            } else {
                LOGGER.info("Not sending email to {} for {} on account of appHost is {}", email, questionIds, appHost);
            }
        } catch (Exception e) {
           LOGGER.error("generate()", e);
        }

        return false;
    }

}
