package ibeere.newsletter.newquestion;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ibeere.user.UserId;

@RestController
@RequiredArgsConstructor
public class NewQuestionsController {
    private final NewQuestionsService newQuestionsService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/newsletter/new-questions")
    public void latestNewQuestions(@RequestParam(required = false) UserId overrideUserId) {
        newQuestionsService.sendEmails(overrideUserId);
    }
}
