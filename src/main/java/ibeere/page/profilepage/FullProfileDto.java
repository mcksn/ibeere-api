package ibeere.page.profilepage;

import lombok.Getter;
import ibeere.aggregate.profile.bio.BioProfile;
import ibeere.aggregate.credential.profile.CredentialProfile;
import ibeere.aggregate.profile.micro.MicroProfile;
import ibeere.user.UserId;

@Getter
public class FullProfileDto extends ProfileDto {
    private String bio;

    public FullProfileDto(MicroProfile microProfile, CredentialProfile credentialProfile, BioProfile bioProfile, UserId requesterId) {
        super(microProfile, credentialProfile, requesterId);
        this.bio = bioProfile.getBio();
    }
}
