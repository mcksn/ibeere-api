package ibeere.user.auth.email;


import org.springframework.security.core.AuthenticationException;
import ibeere.user.UserAlreadyRegisteredException;

public class UserAlreadyRegisteredWithEmailException extends AuthenticationException {
    public UserAlreadyRegisteredWithEmailException(UserAlreadyRegisteredException e) {
        super(e.getEmail());
    }
}
