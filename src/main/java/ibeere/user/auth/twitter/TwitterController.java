package ibeere.user.auth.twitter;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ibeere.user.UserInputException;
import ibeere.user.UserService;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@Transactional
public class TwitterController {

    private UserService userService;

    public TwitterController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/api/v1/auth/twitter/signUp")
    public TwitterRequestToken requestToken(@RequestHeader("first_name") String firstName,
                                            @RequestHeader("last_name") String lastName,
                                            @RequestParam("callbackUrl") String callbackUrl) throws UserInputException {
        return userService.initialTwitterSignUpStage(firstName, lastName, callbackUrl);
    }

    @PostMapping(path = "/api/v1/auth/twitter/login")
    public TwitterRequestToken requestToken(@RequestParam("callbackUrl") String callbackUrl) {
        return userService.initialTwitterLoginStage(callbackUrl);
    }

    @PostMapping(path = "/api/v1/auth/twitter")
    @Deprecated
    public TwitterIdAndAccessToken genAccessToken(@RequestParam("oauth_token") String requestToken, @RequestParam("oauth_verifier") String verifier) {
        return userService.keepAccessCode(TwitterRequestToken.of(requestToken), verifier)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Could not authenticate with verifier"));

    }

    @PostMapping(path = "/api/v1/auth/twitter/access/signUp")
    public TwitterIdAndAccessToken keepAccessCodeFromSignUp(@RequestParam("oauth_token") String requestToken, @RequestParam("oauth_verifier") String verifier) throws TwitterUserAlreadyExistsException {
        return userService.keepAccessCodeFromSignUp(TwitterRequestToken.of(requestToken), verifier)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Could not authenticate with verifier"));

    }

    @PostMapping(path = "/api/v1/auth/twitter/access/login")
    public TwitterIdAndAccessToken keepAccessCodeFromLogin(@RequestParam("oauth_token") String requestToken, @RequestParam("oauth_verifier") String verifier) throws TwitterUserDoesNotExistException {
        return userService.keepAccessCodeFromLogin(TwitterRequestToken.of(requestToken), verifier)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Could not authenticate with verifier"));

    }

}
