package ibeere.aggregate.question.answer;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ibeere.ddd.ValueObject;

@Getter
@RequiredArgsConstructor
public class Image implements ValueObject {
    private final String uri;
    private final int width;
    private final int height;

    public boolean isContain() {
        return width < height;
    }
}