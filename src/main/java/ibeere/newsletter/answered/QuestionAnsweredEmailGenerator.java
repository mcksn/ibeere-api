package ibeere.newsletter.answered;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ibeere.mail.EmailService;
import ibeere.mail.Mail;
import ibeere.aggregate.question.answer.Answer;
import ibeere.aggregate.question.answer.Image;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
public class QuestionAnsweredEmailGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(QuestionAnsweredRebuiltConsumer.class);

    private final String appHost;
    private final EmailService emailService;

    public QuestionAnsweredEmailGenerator(@Value("${app.host}") String appHost, EmailService emailService) {
        this.appHost = appHost;
        this.emailService = emailService;
    }

    public void generate(String email, Answer answer, EmailInterestType interest) {
        LOG.info("Sending email to {} for {}", email,answer.answerQuestionRef());

        Map<String, Object> model = new HashMap();
        model.put("email", email);
        model.put("location", "Nigeria");
        model.put("signature", "https://ibeere.com");
        model.put("profileUrl", answer.getProfile().getUrl());
        model.put("profileImgUrl", ofNullable(answer.getProfile().getImgUrl())
                .map(f -> f.replace("http://", "https://"))
                .orElse(null));
        model.put("content", answer.getContentPreview());
        model.put("bio", ofNullable(answer.getProfile().getCredentials()).orElse(""));
        model.put("userName", answer.getProfile().getName());
        model.put("questionText", answer.getQuestion().getQuestionText());
        model.put("date", answer.getAnsweredActualHumanDate());
        model.put("host", appHost);
        model.put("questionUrl", answer.getQuestion().getUrl());
        model.put("answerTag", answer.getTag());
        model.put("interestType", interest.text());
        model.put("answerImageUrl", Optional.ofNullable(answer.getFirstImage()).map(Image::getUri).orElse(null));

        final Mail mail = Mail.builder()
                .from("team@ibeere.com")
                .to(email)
                .subject(StringUtils.truncate(answer.getQuestion().getQuestionText(), 100))
                .bulk(true)
                .model(model)
                .build();

        try {
            if (!appHost.contains("localhost")) {
                emailService.sendSimpleMessage(mail, "question.answered.ftlh");
            } else {
                LOG.info("Not sending email to {} for {} on account of appHost is {}", email,answer.answerQuestionRef(), appHost);
            }
        } catch (Exception e) {
            LOG.error("generate()", e);
        }

    }
}
