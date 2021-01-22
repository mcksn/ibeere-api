package ibeere.aggregate.question.answer;

import ibeere.aggregate.question.QuestionId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static lombok.AccessLevel.PACKAGE;
import static org.apache.commons.lang3.builder.ToStringStyle.SIMPLE_STYLE;

@AllArgsConstructor
public class AnswerQuestionRef {
    private final AnswerId answerId;
    private final QuestionId questionId;

    public static AnswerQuestionRef of(AnswerId answerId, QuestionId questionId) {
        return new AnswerQuestionRef(answerId, questionId);
    }

    public AnswerId answerId() {
        return answerId;
    }

    public QuestionId questionId() {
        return questionId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, SIMPLE_STYLE);
    }
}
