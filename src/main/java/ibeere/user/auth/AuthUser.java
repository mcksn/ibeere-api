package ibeere.user.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import ibeere.user.UserId;

import java.util.Collection;

public class AuthUser extends org.springframework.security.core.userdetails.User {

    private UserId userId;

    public AuthUser(UserId userId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userId = userId;
    }

    public UserId getUserId() {
        return userId;
    }

    public static UserId userId(Authentication authentication) {
        return ((AuthUser) authentication.getPrincipal()).getUserId();
    }
}
