package ibeere.user.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ibeere.user.auth.email.OTPUser;
import ibeere.user.UserService;

import static java.util.Collections.EMPTY_LIST;

@Service
@Primary
@RequiredArgsConstructor
public class LoginUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final OTPUser otpUser = userService.findOtp(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return new AuthUser(otpUser.getUserId(), username, otpUser.getOtp().getValue(), EMPTY_LIST);
    }
}
