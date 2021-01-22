package ibeere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.format.FormatterRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import ibeere.framework.IdentifierConverter;

@SpringBootApplication
@EnableScheduling
public class Application extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder().build();
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new IdentifierConverter.AnswerIdConverter());
        registry.addConverter(new IdentifierConverter.QuestionIdConverter());
        registry.addConverter(new IdentifierConverter.CommentIdConverter());
        registry.addConverter(new IdentifierConverter.UserIdConverter());
        registry.addConverter(new IdentifierConverter.ProfileIdConverter());
        registry.addConverter(new IdentifierConverter.CredentialIdConverter());
        registry.addConverter(new IdentifierConverter.TwitterUserIdConverter());
    }
}
