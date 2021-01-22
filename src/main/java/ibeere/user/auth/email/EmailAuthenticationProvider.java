package ibeere.user.auth.email;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import ibeere.user.UserService;
import ibeere.user.auth.google.GoogleUserToken;

import java.util.Collections;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EmailAuthenticationProvider implements AuthenticationProvider {
    private static final Logger LOG = LoggerFactory.getLogger(EmailAuthenticationProvider.class);

    private final UserService userService;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        String username = auth.getName();
        String password = auth.getCredentials().toString();

        if (!username.contains("@") || GoogleUserToken.valid(username)) {
            return null;
        }

        OTPUser otpUser = userService.findOtp(auth.getName())
                .orElseThrow(() -> {
                    LOG.warn("Attempted to compare {} otp with provided {}. But no otp found.", username, password);
                    return new BadCredentialsException("External system authentication failed");
                });

        if (!Objects.equals(otpUser.getOtp().getValue(), password)) {
            LOG.warn("Attempted to compare {} otp {} with provided {}. Didnt match", username, otpUser, password);
            throw new OTPDidNotMatchException();
        }

        return new EmailAuthenticationToken(username, password, Collections.EMPTY_LIST);
    }
 
    @Override
    public boolean supports(Class<?> auth) {
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }
}