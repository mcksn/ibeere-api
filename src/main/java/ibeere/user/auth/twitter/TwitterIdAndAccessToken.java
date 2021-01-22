package ibeere.user.auth.twitter;

import lombok.*;

@Getter
@Builder
@RequiredArgsConstructor
public class TwitterIdAndAccessToken {
    private final String twitterId;
    private final String password;
}