package ibeere.newsletter.newquestion;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ibeere.ddd.ValueObject;

@RequiredArgsConstructor
@Getter
public class QuestionModel implements ValueObject {
    private final String text;
    private final String url;
}