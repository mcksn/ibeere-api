package ibeere.aggregate.credential;

import lombok.Getter;
import ibeere.aggregate.profile.micro.MicroProfile;
import ibeere.user.UserId;

import java.util.List;

import static ibeere.aggregate.credential.Credential.asOnAnswerText;

@Getter
public class AnswerCredentialPreviewDto {
    private final UserId userId;
    private final String imgUrl;
    private final String credentials;
    private final String name;

    public AnswerCredentialPreviewDto(MicroProfile profile,
                                      List<Credential> credentials) {
        this.name = profile.getName().full();
        this.credentials = asOnAnswerText(credentials);
        this.imgUrl = profile.getImgUrl();
        this.userId = profile.getUserId();
    }
}
