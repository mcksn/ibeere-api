package ibeere.mail;

import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED;
import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

@Service
public class EmailService {

    private final JavaMailSender emailSender;
    private final Configuration freemarkerConfig;

    public EmailService(JavaMailSender emailSender, @Qualifier("freeMarkerConfiguration") Configuration freemarkerConfig) {
        this.emailSender = emailSender;
        this.freemarkerConfig = freemarkerConfig;
    }

    public void sendSimpleMessage(Mail mail, String templateName) throws MessagingException, IOException, TemplateException {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MULTIPART_MODE_MIXED_RELATED, UTF_8.name());

        if (mail.isBulk()) {
            message.setHeader("List-Unsubscribe", "<mailto: team+unsubscribe@ibeere.com?subject=unsubscribe>");
        }

        freemarkerConfig.setOutputFormat(HTMLOutputFormat.INSTANCE);
        Template template = freemarkerConfig.getTemplate(templateName);
        String html = processTemplateIntoString(template, mail.getModel());

        helper.setTo(mail.getTo());
        final Document document = Jsoup.parse(html);
        document.outputSettings().indentAmount(0).prettyPrint(false);
        helper.setText(ofNullable(mail.getTextVersion()).orElseGet(() -> Jsoup.parse(html).text()), document.html());
        helper.setSubject(mail.getSubject());
        helper.setFrom(mail.getFrom(), "ibeere");

        emailSender.send(message);
    }

}