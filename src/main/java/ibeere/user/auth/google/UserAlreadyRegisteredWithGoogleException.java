package ibeere.user.auth.google;

import org.springframework.security.core.AuthenticationException;
import ibeere.user.UserAlreadyRegisteredException;

public class UserAlreadyRegisteredWithGoogleException extends AuthenticationException {

    public UserAlreadyRegisteredWithGoogleException(UserAlreadyRegisteredException e) {
        super(e.getEmail());
    }
}