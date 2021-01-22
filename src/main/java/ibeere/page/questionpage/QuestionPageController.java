package ibeere.page.questionpage;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ibeere.aggregate.question.*;

import static ibeere.user.auth.AuthUser.userId;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class QuestionPageController {

    private final QuestionPageService questionPageService;

    @GetMapping(path = "/page/question/{path}")
    @PreAuthorize("permitAll()")
    public QuestionDto getQuestionPage(@PathVariable("path") String path) {
        return questionPageService.findQuestionPage(path, null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "None found."));
    }

    @GetMapping(path = "/page/question/{path}/protected")
    @PreAuthorize("isAuthenticated()")
    public QuestionDto getQuestionPage(@PathVariable("path") String path, Authentication authentication) {
        return questionPageService.findQuestionPage(path, userId(authentication))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "None found."));
    }
}
