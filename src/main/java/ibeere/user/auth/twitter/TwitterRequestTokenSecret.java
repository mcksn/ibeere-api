package ibeere.user.auth.twitter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class TwitterRequestTokenSecret {

    @JsonIgnore
    String secret;

    public static TwitterRequestTokenSecret of(String secret) {
        TwitterRequestTokenSecret entityId = new TwitterRequestTokenSecret();
        entityId.secret = secret;
        return entityId;
    }
}