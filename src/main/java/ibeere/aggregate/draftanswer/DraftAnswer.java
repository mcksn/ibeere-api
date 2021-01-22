package ibeere.aggregate.draftanswer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ibeere.ddd.ValueObject;
import ibeere.page.common.LastUpdatedContent;
import ibeere.aggregate.question.QuestionId;

import java.time.Instant;

/**
 * @see DraftAnswerEntity
 */
@Getter
@RequiredArgsConstructor
public class DraftAnswer implements LastUpdatedContent, ValueObject {
    private final QuestionId questionId;
    private final String editorState;
    private final Instant updated;
}
