package ibeere.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import ibeere.user.auth.email.UserAlreadyRegisteredWithEmailException;
import ibeere.user.auth.twitter.UserAlreadyRegisteredWithTwitterException;
import ibeere.user.auth.google.UserAlreadyRegisteredWithGoogleException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (exception instanceof UserAlreadyRegisteredWithGoogleException) {
            response.setStatus(460);
        } else if (exception instanceof UserAlreadyRegisteredWithTwitterException) {
            response.setStatus(461);
        } else if (exception instanceof UserAlreadyRegisteredWithEmailException) {
            response.setStatus(462);
        }
    }
}
