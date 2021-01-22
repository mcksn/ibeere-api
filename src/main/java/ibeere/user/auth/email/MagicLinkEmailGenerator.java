package ibeere.user.auth.email;

import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Service;
import ibeere.mail.EmailService;
import ibeere.mail.Mail;
import ibeere.aggregate.profile.micro.FullName;

@Service
public class MagicLinkEmailGenerator {

    private final EmailService emailService;

    public MagicLinkEmailGenerator(EmailService emailService) {
        this.emailService = emailService;
    }

    public OTP generateMagicLinkEmail(String email, FullName fullName) {

        final OTP otp = OTP.newInstance();

        final Mail mail = Mail.builder()
                .from("team@ibeere.com")
                .to(email)
                .subject("One Time Password Delivered")
                .model(ImmutableMap.of("name", fullName.full(),
                        "email", email,
                        "otp", otp.getValue(),
                        "location", "Nigeria",
                        "signature", "https://ibeere.com"))
                .build();

        try {
            emailService.sendSimpleMessage(mail, "email-template.ftl");
        } catch (Exception e) {
            // TODO handle better
            e.printStackTrace();
        }

        return otp;
    }
}
