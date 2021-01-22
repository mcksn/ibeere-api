package ibeere.aggregate.draftanswer;

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
import ibeere.user.User;
import ibeere.user.UserService;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static ibeere.user.auth.AuthUser.userId;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class DraftAnswerController {

    private final UserService userService;
    private final DraftAnswerService draftAnswerService;

    @PostMapping(path = "/question/{questionId}/answer")
    @PreAuthorize("isAuthenticated()")
    public QuestionId submitDraft(Authentication authentication,
                                  @PathVariable QuestionId questionId,
                                  @RequestBody EditorStateDto editorStateDto) {

        final User userOptional = userService.findById(userId(authentication));
        draftAnswerService.submitDraft(questionId,
                editorStateDto.getCredentialIds(),
                userOptional.getId(), userOptional.getName(), userOptional.getBio(), editorStateDto.getAudience());
        return questionId;
    }

    @PutMapping(path = "/question/{questionId}/draft-answer")
    public UUID updateDraft(Authentication authentication,
                            @PathVariable QuestionId questionId,
                            @RequestBody EditorStateDto editorStateDTO) {
        final User user = userService.findById(userId(authentication));

        return draftAnswerService.updateDraftAnswer(questionId, editorStateDTO.editorState,
                user.getId(), user.getName(), user.getBio(), editorStateDTO.getAudience());
    }

    @GetMapping(path = "/question/{questionId}/draft-answer")
    public EditorStateDto findDraft(Authentication authentication,
                                    @PathVariable QuestionId questionId) {

        return draftAnswerService.findDraftAnswer(questionId, userId(authentication))
                .map(e -> new EditorStateDto(e.getEditorState(), e.getAudience()))
                .orElse(new EditorStateDto());
    }

    @DeleteMapping(path = "/question/{questionId}/draft-answer")
    public boolean deleteDraft(Authentication authentication, @PathVariable QuestionId questionId) {

        draftAnswerService.deleteDraftAnswer(questionId, userId(authentication));
        return true;
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
