package ibeere.aggregate.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ibeere.aggregate.comment.pagination.CommentPaginationPageDto;
import ibeere.aggregate.comment.pagination.CommentPaginationService;
import ibeere.aggregate.question.answer.AnswerId;
import ibeere.aggregate.question.answer.AnswerQuestionRef;
import ibeere.aggregate.question.QuestionId;

import static ibeere.user.auth.AuthUser.userId;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentPaginationService commentPaginationService;
    private final CommentService commentService;


    @GetMapping(path = "/question/{questionId}/answer/{answerId}/comments")
    public CommentPaginationPageDto comments(@PathVariable QuestionId questionId, @PathVariable AnswerId answerId,
                                             @RequestParam(defaultValue = "0") int skipFirstCount) {
        return commentPaginationService.findComments(AnswerQuestionRef.of(answerId, questionId), skipFirstCount, null);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(path = "/question/{questionId}/answer/{answerId}/comments/protected")
    public CommentPaginationPageDto commentsProtected(@PathVariable QuestionId questionId, @PathVariable AnswerId answerId,
                                                      @RequestParam(defaultValue = "0") int skipFirstCount,
                                                      Authentication authentication) {
        return commentPaginationService.findComments(AnswerQuestionRef.of(answerId, questionId), skipFirstCount, userId(authentication));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(path = "/question/{questionId}/answer/{answerId}/comment")
    public CommentDto addComment(@PathVariable QuestionId questionId, @PathVariable AnswerId answerId,
                                       @RequestBody NewCommentInput newComment,
                                       Authentication authentication) {

        final Comment added = commentService.addComment(new NewComment(AnswerQuestionRef.of(answerId, questionId), userId(authentication), newComment.content));
        return new CommentDto(added, userId(authentication));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(path = "/question/{questionId}/answer/{answerId}/comment/{commentId}")
    public boolean deleteComment(@PathVariable QuestionId questionId,
                                 @PathVariable AnswerId answerId,
                                 @PathVariable CommentId commentId,
                                 Authentication authentication) {

        return commentService.deleteComment(commentId, AnswerQuestionRef.of(answerId, questionId), userId(authentication));
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    static class NewCommentInput {
        private String content;
    }
}