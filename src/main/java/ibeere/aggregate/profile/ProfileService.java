package ibeere.aggregate.profile;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ibeere.aggregate.credential.profile.CredentialProfileService;
import ibeere.aggregate.profile.bio.BioProfileService;
import ibeere.aggregate.profile.micro.MicroProfileService;
import ibeere.user.auth.TemporaryUser;
import ibeere.user.UserId;
import ibeere.user.UserInputException;

import java.util.Optional;

/**
 * Service for maintaining transactional consistency of profile updates across bounded context
 * until we an eventually consistent approach is implemented
 */
@Service
@RequiredArgsConstructor
public class ProfileService {
    private static final Logger LOG = LoggerFactory.getLogger(ProfileService.class);

    private final MicroProfileService microProfileService;
    private final CredentialProfileService credentialProfileService;
    private final BioProfileService bioProfileService;

    public Optional<String> editProfile(UserId userId, EditedProfile editedProfile) throws UserInputException {
        bioProfileService.editProfile(userId, editedProfile);
        return microProfileService.editProfile(userId, editedProfile);
    }

    public void newProfile(TemporaryUser user) {
        if (microProfileService.exists(user.getUserId())) {
            LOG.warn("Attempt to add profile {} when already added", user.getUserId());
        }
        microProfileService.newProfile(user);
        credentialProfileService.newProfile(user);
    }
}
