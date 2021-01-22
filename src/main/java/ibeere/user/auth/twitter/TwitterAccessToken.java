package ibeere.user.auth.twitter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
@Getter
@EqualsAndHashCode
public class TwitterAccessToken {
    private final String value;
    private final String secret;

    public static TwitterAccessToken deserialized(String raw) {
        final String[] rawSplit = raw.split("___");
        return new TwitterAccessToken(rawSplit[0], rawSplit[1]);
    }

    public String serialized() {
        return value + "___" + secret;
    }
}
