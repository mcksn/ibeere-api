package ibeere.mail;

import com.google.common.base.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Builder @Getter @Setter @ToString
public class Mail {

    private final String from;
    private final String to;
    private final String subject;
    private final String content;
    private final Map<String, Object> model;
    private final String textVersion;
    private final boolean bulk;

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("from", from)
                .add("to", to)
                .add("subject", subject)
                .add("content", content)
                .add("model", model)
                .add("textVersion", textVersion)
                .add("bulk", bulk)
                .toString();
    }
}