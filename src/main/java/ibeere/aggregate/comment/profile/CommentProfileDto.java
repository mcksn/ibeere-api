package ibeere.aggregate.comment.profile;

import lombok.Getter;
import ibeere.user.UserId;

@Getter
public class CommentProfileDto {
    private final UserId userId;
    private final String name;
    private final String imgUrl;
    private final String path;
    private final boolean verified;
    private final String url;

    public CommentProfileDto(CommentProfile profile) {
        this.userId = profile.getUserId();
        this.url = profile.getUrl();
        this.path = profile.getPath();
        this.name = profile.getName();
        this.verified = profile.isVerified();
        this.imgUrl = profile.getImgUrl();
    }
}
