package ibeere.user.auth.twitter;

import org.springframework.security.core.AuthenticationException;
import ibeere.user.UserAlreadyRegisteredException;

public class UserAlreadyRegisteredWithTwitterException extends AuthenticationException {

    public UserAlreadyRegisteredWithTwitterException(UserAlreadyRegisteredException e) {
        super(e.getEmail());
    }
}