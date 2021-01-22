package ibeere.page.profilepage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ibeere.aggregate.credential.AnswerCredentialPreviewDto;
import ibeere.aggregate.credential.CredentialId;
import ibeere.aggregate.credential.CredentialType;
import ibeere.aggregate.credential.profile.CredentialDto;
import ibeere.aggregate.credential.profile.CredentialProfileService;
import ibeere.aggregate.profile.EditedProfile;
import ibeere.aggregate.profile.ProfileService;
import ibeere.aggregate.profile.micro.FullName;
import ibeere.aggregate.profile.micro.MicroProfileDto;
import ibeere.aggregate.profile.micro.MicroProfileService;
import ibeere.aggregate.question.answer.AnswerDto;
import ibeere.aggregate.question.QuestionDto;
import ibeere.user.UserId;
import ibeere.user.UserInputException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static ibeere.user.auth.AuthUser.userId;

@RestController
@Transactional
@RequiredArgsConstructor
public class ProfilePageController {

    private final CredentialProfileService credentialProfileService;
    private final MicroProfileService microProfileService;
    private final ProfileService profileService;
    private final ProfilePageService profilePageService;

    @PutMapping(path = "/profile/img")
    @PreAuthorize("isAuthenticated()")
    public boolean changeImg(Authentication authentication,
                             @RequestParam Optional<UserId> userId,
                             @RequestBody Img img) {
        microProfileService.changeImg(userId.orElse(userId(authentication)), img.img);
        return true;
    }

    @PutMapping(path = "/profile")
    @PreAuthorize("isAuthenticated()")
    public ProfileEditResponseBody editMyProfile(Authentication authentication,
                                                 @RequestParam Optional<UserId> userId,
                                                 @RequestBody ProfileEditRequestBody responseBody) throws UserInputException {
        return profileService.editProfile(userId.orElse(userId(authentication)), new EditedProfile(responseBody.bio, FullName.of(responseBody.firstName, responseBody.lastName)))
                .map(ProfileEditResponseBody::new)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "None found."));
    }

    @PutMapping(path = "/profile/credential/{credentialId}")
    @PreAuthorize("isAuthenticated()")
    public boolean updateTemplateCredential(Authentication authentication,
                                          @PathVariable CredentialId credentialId,
                                          @RequestParam Optional<UserId> userId,
                                          @RequestBody UpdateCredentialRequestBody requestBody) throws UserInputException {
        credentialProfileService.updateTemplateCredential(credentialId, userId(authentication), userId.orElse(userId(authentication)), requestBody.text);

        return true;
    }

    @PostMapping(path = "/profile/credential")
    @PreAuthorize("isAuthenticated()")
    public boolean addTemplateCredential(Authentication authentication,
                                          @RequestParam Optional<UserId> userId,
                                          @RequestBody NewCredentialRequestBody requestBody) throws UserInputException {
        credentialProfileService.addTemplateCredential(requestBody.getCredentialType(), userId(authentication), userId.orElse(userId(authentication)), requestBody.text);

        return true;
    }

    @DeleteMapping(path = "/profile/credential/{credentialId}")
    @PreAuthorize("isAuthenticated()")
    public boolean removeTemplateCredential(Authentication authentication,
                                            @PathVariable CredentialId credentialId,
                                            @RequestParam Optional<UserId> userId) {
        credentialProfileService.removeTemplateCredential(credentialId, userId(authentication), userId.orElse(userId(authentication)));

        return true;
    }

    @GetMapping(path = "__/update-view-credential")
    @PreAuthorize("isAuthenticated()")
    public boolean updateViewsCredential(Authentication authentication) {
        credentialProfileService.updateViewsCredential();
        return true;
    }

    @GetMapping(path = "__/update-answered-credential")
    @PreAuthorize("isAuthenticated()")
    public boolean updateAnsweredCredential(Authentication authentication) {
        credentialProfileService.updateAnsweredCredential();
        return true;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(path = "/page/profile/{path}")
    public FullProfileDto profilePage(Authentication authentication, @PathVariable String path) {
        return profilePageService.findFullByPathUncached(path, userId(authentication));
    }

    @GetMapping(path = "/page/profile/{path}/guest")
    public ProfileDto guestProfilePage(@PathVariable String path) {
        return profilePageService.findProfilePage(path);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(path = "/profile/micro")
    public MicroProfileDto getMyProfile(Authentication authentication) {
        return microProfileService.findMicroBy(userId(authentication))
                .map(MicroProfileDto::new)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "None found."));

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(path = "/profile/credential/preview")
    public AnswerCredentialPreviewDto previewCredentials(@RequestParam Optional<UserId> userId,
                                                         Authentication authentication,
                                                         @RequestParam Set<CredentialId> credentialIds) {
        return profilePageService.findBy(userId.orElse(userId(authentication)), credentialIds);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(path = "/profile/credential/options")
    public List<CredentialDto> credentialOptions(@RequestParam Optional<UserId> userId, Authentication auth) {
        return profilePageService.findCredentialOptions(userId.orElse(userId(auth)), userId(auth));
    }

    @GetMapping(path = "/profile/{userId}/answers")
    public List<AnswerDto> getAnswer(@PathVariable UserId userId) {
        return profilePageService.findProfileAnswers(userId);
    }

    @GetMapping(path = "/profile/{userId}/questions/qanda")
    public List<QuestionDto> qandAQuestions(@PathVariable UserId userId) {
        return profilePageService.findProfileQandAQuestions(userId);
    }

    @GetMapping(path = "/profile/{userId}/questions")
    public List<QuestionDto> questions(@PathVariable UserId userId) {
        return profilePageService.findProfileQuestions(userId);
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Img {
        String img;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProfileEditRequestBody {

        private String bio;
        private String firstName;
        private String lastName;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProfileEditResponseBody {
        private String path;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateCredentialRequestBody {
        private String text;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NewCredentialRequestBody {
        private String text;
        private CredentialType credentialType;
    }
}
