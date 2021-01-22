package ibeere.aggregate.comment.pagination;

import ibeere.aggregate.comment.CommentDto;

import java.util.List;

public class CommentPaginationPageDto {
    private List<CommentDto> comments;
    private boolean hasMore;

    public CommentPaginationPageDto(List<CommentDto> comments, boolean hasMore) {
        this.comments = comments;
        this.hasMore = hasMore;
    }

    public List<CommentDto> getComments() {
        return comments;
    }

    public boolean isHasMore() {
        return hasMore;
    }
}
