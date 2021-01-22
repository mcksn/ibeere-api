package ibeere.user.auth.email;

import org.springframework.security.core.AuthenticationException;

public class OTPDidNotMatchException extends AuthenticationException {
    public OTPDidNotMatchException() {
        super("otp did not match");
    }
}
