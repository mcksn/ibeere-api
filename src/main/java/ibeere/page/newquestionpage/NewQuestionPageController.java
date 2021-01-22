package ibeere.page.newquestionpage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ibeere.audience.Audience;
import ibeere.aggregate.credential.CredentialId;
import ibeere.page.questionpage.QuestionPageService;
import ibeere.aggregate.profile.micro.MicroProfileService;
import ibeere.aggregate.question.*;
import ibeere.aggregate.question.answer.AnswerViewCountService;
import ibeere.questiondoc.QuestionDocumentService;
import ibeere.user.User;
import ibeere.user.UserDocumentService;
import ibeere.user.UserService;

import java.util.Optional;
import java.util.Set;

import static ibeere.user.auth.AuthUser.userId;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class NewQuestionPageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewQuestionPageController.class);

    private final UserService userService;
    private final QuestionService questionService;
    private final QuestionPageService questionPageService;
    private final QuestionDocumentService questionDocumentService;
    private final UserDocumentService userDocumentService;
    private final AnswerViewCountService answerViewCountService;
    private final MicroProfileService microProfileService;

    @PostMapping(path = "/question/submit")
    @PreAuthorize("isAuthenticated()")
    public QuestionId submitQuestion(Authentication authentication,
                                     @RequestBody QuestionInput questionInput) throws Exception {
        final User userOptional = userService.findById(userId(authentication));

        final QuestionId questionId = questionService.submitQuestion(
                questionInput.questionText,
                questionInput.linkText,
                userId(authentication),
                userOptional.getName(),
                questionInput.audience, Optional.ofNullable(questionInput.qAndAProfilePath)
                        .flatMap(microProfileService::findMicroByPath).orElse(null));
        this.questionDocumentService.rebuild(questionId);
        this.userDocumentService.rebuild(userId(authentication), questionId);
        return questionId;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestionInput {
        private String questionText;
        private String linkText;
        private Audience audience;
        private String qAndAProfilePath;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    static class EditorStateDto {
        String editorState;
        Audience audience;
        Set<CredentialId> credentialIds;

        public EditorStateDto(String editorState, Audience audience) {
            this.editorState = editorState;
            this.audience = audience;
        }
    }
}
