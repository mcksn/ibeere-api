package ibeere.aggregate.profile.micro;

import lombok.Getter;
import ibeere.user.UserId;

@Getter
public class MicroProfileDto {
    private UserId userId;
    private String firstName;
    private String lastName;
    private String name;
    private String imgUrl;
    private String path;

    public MicroProfileDto(MicroProfile profile) {
        this.firstName = profile.getName().first();
        this.lastName = profile.getName().last();
        this.imgUrl = profile.getImgUrl();
        this.path = profile.getPath();
        this.userId = profile.getUserId();
        this.name = profile.getName().full();
    }
    public String getUrl() {
        return new StringBuilder().append("https://ibeere.com").append("/profile/").append(path).toString();
    }
}
