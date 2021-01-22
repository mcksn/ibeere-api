package ibeere.aggregate.comment.profile;

import lombok.Getter;
import ibeere.ddd.ValueObject;
import ibeere.aggregate.profile.micro.MicroProfile;
import ibeere.user.UserId;

@Getter
public class CommentProfile implements ValueObject {
    private final UserId userId;
    private final String name;
    private final String imgUrl;
    private final String path;
    private final boolean verified;
    private final String url;

    public CommentProfile(MicroProfile profile) {
            this.userId = profile.getUserId();
            this.url = profile.getUrl();
            this.path = profile.getPath();
            this.name = profile.getName().full();
            this.verified = profile.isVerified();
            this.imgUrl = profile.getImgUrl();
    }
}
