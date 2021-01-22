package ibeere.user.auth.google;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import ibeere.user.*;
import ibeere.user.auth.AuthType;
import ibeere.user.auth.email.UserAlreadyRegisteredWithEmailException;
import ibeere.user.auth.twitter.UserAlreadyRegisteredWithTwitterException;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GoogleAuthenticationProvider implements AuthenticationProvider {

    private final GoogleUserService googleUserService;
    private final UserService userService;

    @Override
    public Authentication authenticate(Authentication auth)
            throws AuthenticationException {
        String username = auth.getName();

        if (!GoogleUserToken.valid(username)) {
            return null;
        }

        try {
            GoogleUser googleUser = googleUserService.upsert(GoogleUserToken.of(auth.getName()));
            return new GoogleAuthenticationToken(googleUser.getGoogleUserId().getId(), UserEntity.DEFAULT_PASSWORD, Collections.EMPTY_LIST);
        } catch (UserAlreadyRegisteredException exception) {
            AuthType authType = userService.whichAuthType(exception.getEmail());
            if (authType == AuthType.GOOGLE) {
                throw new UserAlreadyRegisteredWithGoogleException(exception);
            } else if (authType == AuthType.EMAIL) {
                throw new UserAlreadyRegisteredWithEmailException(exception);
            } else {
                throw new UserAlreadyRegisteredWithTwitterException(exception);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean supports(Class<?> auth) {
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }
}