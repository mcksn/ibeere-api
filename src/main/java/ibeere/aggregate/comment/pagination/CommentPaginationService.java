package ibeere.aggregate.comment.pagination;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ibeere.aggregate.comment.CommentDto;
import ibeere.aggregate.comment.CommentService;
import ibeere.aggregate.question.answer.AnswerQuestionRef;
import ibeere.user.UserId;

import static java.util.stream.Collectors.toList;


@Service(value = "commentDocumentService")
public class CommentPaginationService {

    @Autowired
    private CommentService commentService;

    public CommentPaginationPageDto findComments(AnswerQuestionRef answerQuestionRef, int skipFirstCount, UserId requesterId) {

        final CommentPaginationPage commentPaginationPage = commentService.findComments(answerQuestionRef, skipFirstCount);

        return new CommentPaginationPageDto(commentPaginationPage.getComments().stream()
                .map(comment -> new CommentDto(comment, requesterId))
                .collect(toList()), commentPaginationPage.isHasMore());
    }
}
