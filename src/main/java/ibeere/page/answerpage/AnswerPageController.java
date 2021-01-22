package ibeere.page.answerpage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ibeere.audience.Audience;
import ibeere.aggregate.credential.CredentialId;
import ibeere.aggregate.question.*;
import ibeere.aggregate.question.answer.AnswerId;
import ibeere.aggregate.question.answer.AnswerQuestionRef;
import ibeere.aggregate.question.answer.AnswerService;

import java.util.List;
import java.util.Set;

import static ibeere.user.auth.AuthUser.userId;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class AnswerPageController {

    private final AnswerService answerService;
    private final AnswerPageService answerPageService;

    @GetMapping(path = "/page/answer")
    @PreAuthorize("isAuthenticated()")
    public List<QuestionDto> answerPage(Authentication authentication) {
        return answerPageService.findAnswerPage(userId(authentication));
    }

    @PutMapping(path = "/question/{questionId}/answer/{answerId}/content")
    @PreAuthorize("isAuthenticated()")
    public boolean updateAnswerContent(Authentication authentication,
                                     @PathVariable QuestionId questionId,
                                     @PathVariable AnswerId answerId,
                                     @RequestBody EditorStateDto editorStateDto) {
        return answerService.updateAnswerContent(AnswerQuestionRef.of(answerId, questionId), editorStateDto.editorState, userId(authentication));
    }

    @GetMapping(path = "/question/{questionId}/answer/{answerId}/content")
    @PreAuthorize("isAuthenticated()")
    public EditorStateDto findAnswerContent(Authentication authentication,
                                            @PathVariable QuestionId questionId,
                                            @PathVariable AnswerId answerId) {

        return answerService.findAnswerContent(AnswerQuestionRef.of(answerId, questionId), userId(authentication))
                .map(e -> new EditorStateDto(e.getEditorState().replace("http://","https://"), e.getAudience()))
                .orElse(new EditorStateDto());
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
