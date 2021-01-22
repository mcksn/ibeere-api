package ibeere.sitemap;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ibeere.aggregate.profile.micro.MicroProfile;
import ibeere.aggregate.profile.micro.MicroProfileService;
import ibeere.aggregate.question.Question;
import ibeere.questiondoc.QuestionDocumentService;
import ibeere.aggregate.question.QuestionService;

import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

@RestController
@RequiredArgsConstructor
public class SitemapController {

    private final QuestionService questionService;
    private final MicroProfileService microProfileService;
    private final QuestionDocumentService questionDocumentService;

    @RequestMapping(value = "/sitemap.txt", method = RequestMethod.GET)
    public String sitemap() {

        final StringBuilder stringBuilder = new StringBuilder("https://ibeere.com")
                .append("\n")
                .append("https://ibeere.com/faq")
                .append("\n")
                .append("https://ibeere.com/about")
                .append("\n");
        final String questions = questionService.findAll().stream()
                .map(questionDocumentService::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Question::getUrl)
                .collect(joining("\n"));

        stringBuilder.append(questions);
        stringBuilder.append("\n");
        final String profiles = microProfileService.findAllMicro().stream()
                .filter(f -> !Objects.equals(f.getName().last(), "t"))
                .map(MicroProfile::getUrl)
                .collect(joining("\n"));
        stringBuilder.append(profiles);
        return stringBuilder.toString();
    }
}
