package ibeere.page.profilepage;

import lombok.Getter;
import ibeere.aggregate.credential.profile.CredentialDto;
import ibeere.aggregate.credential.profile.CredentialProfile;
import ibeere.aggregate.profile.micro.MicroProfile;
import ibeere.user.UserId;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
public class ProfileDto {
    private final UserId userId;
    private final String firstName;
    private final String lastName;
    private final String name;
    private final String imgUrl;
    private final String path;
    private final boolean requesterOwner;
    private final List<CredentialDto> credentials;
    private final boolean verified;

    public ProfileDto(MicroProfile microProfile, CredentialProfile credentialProfile, UserId requesterId) {
        this.firstName = microProfile.getName().first();
        this.lastName = microProfile.getName().last();
        this.imgUrl = microProfile.getImgUrl();
        this.path = microProfile.getPath();
        this.verified = microProfile.isVerified();
        this.requesterOwner = microProfile.getUserId().equals(requesterId);
        this.userId = microProfile.getUserId();
        this.name = microProfile.getName().full();
        this.credentials = credentialProfile.getCredentialOptionsForAnAnswer().stream()
                .map(CredentialDto::new)
                .collect(toList());
    }
}
