package ibeere.user;

import ibeere.ddd.ImmutableEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ibeere.ddd.ValueObject;

@Getter
@RequiredArgsConstructor
public class CookieUser implements ImmutableEntity {

    private final UserId userId;
    private final String cookie;
}
