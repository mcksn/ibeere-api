package ibeere.user.auth.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ibeere.user.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

@Service
public class GoogleUserService {

    private final GoogleIdTokenVerifier verifier;
    private final UserService userService;

    public GoogleUserService(@Value("${google.oauth.clientId:806926975181-8ogn751g92sqefm12bi6f84kn9o73m24.apps.googleusercontent.com}")
                                     String clientId, UserService userService) {
        this.verifier = new GoogleIdTokenVerifier.Builder(new ApacheHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(clientId))
                .build();
        this.userService = userService;
    }

    public GoogleUser upsert(GoogleUserToken appGoogleUserToken) throws GeneralSecurityException, IOException, UserAlreadyRegisteredException {
        GoogleIdToken idToken = verifier.verify(appGoogleUserToken.getOriginal());

        if (idToken == null) {
            throw new RuntimeException("Google Id Token was null");
        }
        GoogleIdToken.Payload payload = idToken.getPayload();

        // Print user identifier
        String userId = payload.getSubject();

        // Get profile information from payload
        String email = payload.getEmail();
        boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
        String name = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");
        String locale = (String) payload.get("locale");
        String familyName = (String) payload.get("family_name");
        String givenName = (String) payload.get("given_name");

        User user;
        Optional<User> userMaybe = userService.findByGoogleUserId(GoogleUserId.of(userId));

        if (userMaybe.isPresent()) {
            user = userMaybe.get();
        } else {
            user = userService.newUser(GoogleUserId.of(userId), givenName, familyName, email.toLowerCase(), pictureUrl);
        }
        return new GoogleUser(user.getId(), GoogleUserId.of(userId), email);
    }
}
