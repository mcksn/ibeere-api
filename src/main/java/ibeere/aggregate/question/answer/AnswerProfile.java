package ibeere.aggregate.question.answer;

import lombok.Getter;
import ibeere.aggregate.credential.Credential;
import ibeere.ddd.ImmutableEntity;
import ibeere.aggregate.profile.micro.MicroProfile;
import ibeere.audience.Audience;
import ibeere.user.UserId;

import java.util.List;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static ibeere.audience.Audience.ANONYMOUS;

@Getter
public class AnswerProfile implements ImmutableEntity {
    private final UserId userId;
    private final String credentials;
    private final String name;
    private final String imgUrl;
    private final String path;
    private final boolean verified;
    private final String url;

    public AnswerProfile(MicroProfile microProfile,
                         List<Credential> credentials,
                         Audience audience) {
        if (audience == ANONYMOUS) {
            this.name = "Anonymous";
            this.credentials = "";
            this.userId = null;
            this.verified = false;
            this.url = "";
            this.imgUrl = null;
            this.path = null;
        } else {
            this.userId = microProfile.getUserId();
            this.url = microProfile.getUrl();
            this.path = microProfile.getPath();
            this.name = microProfile.getName().full();
            this.verified = microProfile.isVerified();
            this.credentials = Credential.asOnAnswerText(credentials);
            this.imgUrl = microProfile.getImgUrl();
        }
    }

    public String getImgUrl() {
        return ofNullable(imgUrl).map(f -> f.replace("http://", "https://")
                // replacing for reverse proxy TODO make cleaner and re-useable
                .replaceAll("res.cloudinary.com", "ibeere.com"))
                .orElse(null);
    }
}
