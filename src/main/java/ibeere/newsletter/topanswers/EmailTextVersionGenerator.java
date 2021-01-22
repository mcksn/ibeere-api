package ibeere.newsletter.topanswers;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
class EmailTextVersionGenerator {
    public String generate(String subject, List<AnswerModel> answers) {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(subject);
        stringBuilder.append("\n");
        stringBuilder.append("\n");
        stringBuilder.append("-------");
        answers.forEach(a -> {
            stringBuilder.append("\n");
            stringBuilder.append("\n");
            stringBuilder.append("Question: " + a.getQuestionText());
            stringBuilder.append("\n");
            stringBuilder.append("\n");
            stringBuilder.append("Answer from " + a.getProfileName());
            stringBuilder.append("\n");
            stringBuilder.append("\n");
            stringBuilder.append(a.getContent());
            stringBuilder.append("\n");
            stringBuilder.append("Read More: " + a.getQuestionUrl() + "?utm_medium=email&utm_source=top.answers&utm_term=read-more#" + a.getAnswerTag());
            stringBuilder.append("\n");
            stringBuilder.append("------");
        });

        return stringBuilder.toString();
    }
}
