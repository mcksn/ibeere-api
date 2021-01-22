package ibeere.aggregate.comment.pagination;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ibeere.aggregate.comment.Comment;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CommentPaginationPage {
    private final List<Comment> comments;
    private final boolean hasMore;
}
