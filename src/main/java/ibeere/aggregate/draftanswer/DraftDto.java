package ibeere.aggregate.draftanswer;

import lombok.Getter;
import ibeere.page.common.LastUpdatedContent;
import ibeere.aggregate.question.Question;
import ibeere.aggregate.question.QuestionDto;

import java.time.Instant;

@Getter
public class DraftDto implements LastUpdatedContent {
    private final QuestionDto question; //without answers
    private final String editorState;
    private final Instant updated;

    public DraftDto(DraftAnswer draftAnswer, Question question) {
        this.question = QuestionDto.toQuestionDtoWithNoAnswers(question);
        this.editorState = draftAnswer.getEditorState();
        this.updated = draftAnswer.getUpdated();
    }
}
