package ibeere.newsletter.topanswers;

import lombok.Builder;
import lombok.Getter;
import ibeere.ddd.ValueObject;

@Builder
@Getter
class AnswerModel implements ValueObject {
    private final String profileImgUrl;
    private final String profileUrl;
    private final String content;
    private final String credentials;
    private final String profileName;
    private final String questionText;
    private final String date;
    private final String host;
    private final String questionUrl;
    private final String answerTag;
    private final String answerImageUrl;
}

