package ibeere.aggregate.profile.micro;

import lombok.Getter;
import ibeere.aggregate.profile.QandAStatus;
import ibeere.user.UserId;

import java.util.Optional;

/** Profile of a user */
@Getter
public class MicroProfile {
    private final UserId userId;
    private final FullName name;
    private final String imgUrl;
    private final String path;
    private final QandAStatus qandAStatus; // TODO move  qAndA stuff into another aggregate
    private final boolean verified;

    public MicroProfile(UserId userId, FullName name, String imgUrl, String path, QandAStatus qandAStatus, boolean verified) {
        this.userId = userId;
        this.name = name;
        this.imgUrl = Optional.ofNullable(imgUrl)
                // replacing for reverse proxy TODO make cleaner and re-useable
                .map(s -> s.replaceAll("res.cloudinary.com", "ibeere.com"))
                .orElse(null);
        this.path = path;
        this.qandAStatus = qandAStatus;
        this.verified = verified;
    }

    public String getUrl() {
        return "https://ibeere.com" + "/profile/" + path;
    }

    public String getImgUrl() {
        return Optional.ofNullable(imgUrl)
                // replacing for reverse proxy TODO make cleaner and re-useable
                .map(f -> f.replace("http://", "https://").replaceAll("res.cloudinary.com", "ibeere.com"))
                .orElse(null);
    }

    /**
     * @see ibeere.aggregate.profile.QandAStatus
     */
    public boolean isDoingAQandA() {
        return qandAStatus != null;
    }
}

