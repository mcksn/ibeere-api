package ibeere.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ibeere.user.auth.AuthUser;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(path = "/otp/{email}")
    public Long newOtpForNewUser(@PathVariable("email") String email, @RequestBody UserInput userInput) throws UserInputException {
        return userService.newEmailOtpForNewUser(email, userInput.firstName, userInput.getLastName(), userInput.getBio());
    }

    @PutMapping(path = "/otp/{email}")
    public Long newOtpForExistingUser(@PathVariable("email") String email) {
        return userService.newOtpForExistingUser(email);
    }

    @GetMapping(path = "/user/name")
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public User findName(Authentication authentication) {
        return new User(userService.findById(AuthUser.userId(authentication)).getFullName().full());
    }

    @Getter
    @AllArgsConstructor
    public static class User {
        private String userName;
    }

    @Getter
    @AllArgsConstructor
    public static class OtpInput {
        private String otp;
    }

    @Getter
    @AllArgsConstructor
    public static class UserInput {
        private String firstName;
        private String lastName;
        private String bio;
    }
}
