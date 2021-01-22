package ibeere.page.profilepage;

import lombok.Getter;
import ibeere.aggregate.profile.bio.BioProfile;
import ibeere.aggregate.credential.profile.CredentialProfile;
import ibeere.aggregate.profile.micro.MicroProfile;
import ibeere.user.UserId;

@Getter
public class QandAProfileDto extends FullProfileDto {

    private final boolean qandARunning;
    private final String qandAStatus;

    public QandAProfileDto(MicroProfile microProfile, CredentialProfile credentialProfile, BioProfile bioProfile, UserId requesterId) {
        super(microProfile, credentialProfile, bioProfile, requesterId);
        this.qandARunning = microProfile.isDoingAQandA();
        this.qandAStatus = microProfile.getQandAStatus().name().replace("_", " ");
    }
}
