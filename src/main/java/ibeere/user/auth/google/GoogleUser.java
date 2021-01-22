package ibeere.user.auth.google;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ibeere.user.UserId;

@RequiredArgsConstructor
@Getter
public class GoogleUser {
    private final UserId userId;
    private final GoogleUserId googleUserId;
    private final String email;
}
