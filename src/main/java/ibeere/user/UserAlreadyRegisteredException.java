package ibeere.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserAlreadyRegisteredException extends Exception {
    private final String email;

    public String getEmail() {
        return email;
    }
}
