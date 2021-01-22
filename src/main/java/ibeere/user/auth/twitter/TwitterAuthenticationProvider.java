package ibeere.user.auth.twitter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import ibeere.user.UserService;
import ibeere.user.auth.google.GoogleUserToken;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class TwitterAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        String username = auth.getName();
        String password = auth.getCredentials().toString();

        if (username.contains("@") || GoogleUserToken.valid(username)) {
            return null;
        }

        userService.findAccessToken(TwitterUserId.of(Long.valueOf(username)), TwitterAccessToken.deserialized(password))
                .orElseThrow(() -> new BadCredentialsException("External system authentication failed"));

        return new TwitterAuthenticationToken(username, password, Collections.EMPTY_LIST);
    }
 
    @Override
    public boolean supports(Class<?> auth) {
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }
}