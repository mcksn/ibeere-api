package ibeere.user.auth.twitter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor(staticName = "of")
public class TwitterRequestToken {

    @JsonProperty("oauth_token")
    private final String value;

    public String getValue() {
        return value;
    }
}
