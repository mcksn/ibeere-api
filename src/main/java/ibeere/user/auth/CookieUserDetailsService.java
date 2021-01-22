package ibeere.user.auth;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ibeere.user.*;
import ibeere.user.auth.google.GoogleUserId;
import ibeere.user.auth.twitter.TwitterUserId;

import static java.util.Collections.EMPTY_LIST;

@Service
@RequiredArgsConstructor
public class CookieUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        final CookieUser cookieUser;
         if (GoogleUserId.valid(username)) {
             cookieUser = userService.findCookieSecretByGoogleUserId(GoogleUserId.of(username))
                     .orElseThrow(() -> new UsernameNotFoundException(username));
         } else if (!StringUtils.contains(username, "@")) {
             cookieUser = userService.findCookieSecretByTwitterUserId(TwitterUserId.of(Long.valueOf(username)))
                     .orElseThrow(() -> new UsernameNotFoundException(username));
         } else {
             cookieUser = userService.generateUserNameAndPassword(username).orElseThrow(() -> new UsernameNotFoundException(username));
         }

        return new AuthUser(cookieUser.getUserId(), username, cookieUser.getCookie(), EMPTY_LIST);
    }
}
