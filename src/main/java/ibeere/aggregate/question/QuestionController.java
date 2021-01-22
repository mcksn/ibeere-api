package ibeere.aggregate.question;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ibeere.aggregate.question.answer.AnswerService;
import ibeere.page.questionpage.QuestionPageService;
import ibeere.aggregate.question.answer.AnswerId;
import ibeere.aggregate.question.answer.AnswerQuestionRef;
import ibeere.aggregate.question.answer.AnswerViewCountService;
import ibeere.user.UserInputException;
import ibeere.user.UserService;

import java.util.UUID;

import static ibeere.user.auth.AuthUser.userId;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class QuestionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionController.class);

    private final UserService userService;
    private final QuestionPageService questionPageService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final AnswerViewCountService answerViewCountService;

    @GetMapping(path = "/question/{path}/protected")
    @PreAuthorize("isAuthenticated()")
    public QuestionDto getQuestion(@PathVariable("path") String path, Authentication authentication) {
        return questionPageService.findQuestionPage(path, userId(authentication)).orElse(null);
    }

    @GetMapping(path = "/question/{path}")
    @PreAuthorize("permitAll()")
    public QuestionDto getQuestion(@PathVariable("path") String path) {
        return questionPageService.findQuestionPage(path, null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "None found."));
    }

    @DeleteMapping(path = "/question/{questionId}/follow")
    @PreAuthorize("isAuthenticated()")
    public boolean unFollow(Authentication authentication,
                            @PathVariable("questionId") QuestionId questionId) {
        return questionService.unFollow(userId(authentication), questionId);
    }

    @PostMapping(path = "/question/{questionId}/follow")
    @PreAuthorize("isAuthenticated()")
    public UUID follow(Authentication authentication, @PathVariable("questionId") QuestionId questionId) throws UserInputException {
        return questionService.follow(userId(authentication), questionId);
    }

    @PostMapping(path = "/question/{questionId}/{answerId}/readMore")
    public boolean readMoreView(Authentication authentication, @PathVariable("questionId") QuestionId questionId, @PathVariable AnswerId answerId) {
        LOGGER.info("Read more on {}", AnswerQuestionRef.of(answerId, questionId));
        answerViewCountService.incAndShowViewCount(AnswerQuestionRef.of(answerId, questionId));
        return true;
    }

    @PostMapping(path = "/question/{questionId}/answer/{answerId}/upVote")
    @PreAuthorize("isAuthenticated()")
    public boolean upVote(Authentication authentication,
                             @PathVariable("questionId") QuestionId questionId,
                             @PathVariable("answerId") AnswerId answerId) throws UserInputException {
        return answerService.upVote(userId(authentication), AnswerQuestionRef.of(answerId, questionId));
    }

    @PostMapping(path = "/question/{questionId}/answer/{answerId}/downVote")
    @PreAuthorize("isAuthenticated()")
    public boolean downVote(Authentication authentication,
                                     @PathVariable("answerId") AnswerId answerId, @PathVariable QuestionId questionId) throws UserInputException {

        answerService.downVote(userId(authentication), AnswerQuestionRef.of(answerId, questionId));
        return true;
    }

    @DeleteMapping(path = "/question/{questionId}/answer/{answerId}/downVote")
    @PreAuthorize("isAuthenticated()")
    public boolean cancelDownVote(Authentication authentication,
                                  @PathVariable("answerId") AnswerId answerId, @PathVariable QuestionId questionId) {
        answerService.cancelDownVote(userId(authentication), AnswerQuestionRef.of(answerId, questionId));
        return true;
    }

    @DeleteMapping(path = "/question/{questionId}/answer/{answerId}/upVote")
    @PreAuthorize("isAuthenticated()")
    public boolean cancelUpVote(Authentication authentication,
                                @PathVariable("answerId") AnswerId answerId, @PathVariable QuestionId questionId) {
        answerService.cancelUpVote(userId(authentication), AnswerQuestionRef.of(answerId, questionId));
        return true;
    }
}
