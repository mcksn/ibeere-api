package ibeere.aggregate.comment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ibeere.ddd.ValueObject;
import ibeere.aggregate.question.answer.AnswerQuestionRef;
import ibeere.user.UserId;

@Getter
@RequiredArgsConstructor
public class NewComment implements ValueObject {
    private final AnswerQuestionRef answerIdQuestionRef;
    private final UserId userId;
    private final String content;
}
