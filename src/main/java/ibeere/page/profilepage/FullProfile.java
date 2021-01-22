package ibeere.page.profilepage;

import lombok.Getter;
import ibeere.aggregate.profile.QandAStatus;
import ibeere.aggregate.credential.profile.TemplateCredential;
import ibeere.aggregate.profile.micro.FullName;
import ibeere.aggregate.profile.micro.MicroProfile;
import ibeere.user.UserId;

import java.util.List;

@Getter
public class FullProfile extends MicroProfile {
    private final String bio;
    private final List<TemplateCredential> templateCredentials;

    public FullProfile(UserId userId, FullName name, String imgUrl, String path, QandAStatus qandAStatus, boolean verified, List<TemplateCredential> templateCredentials, String bio) {
        super(userId, name, imgUrl, path, qandAStatus, verified);
        this.bio = bio;
        this.templateCredentials = templateCredentials;
    }
}
