package ibeere.aggregate.profile.bio;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ibeere.aggregate.profile.EditedProfile;
import ibeere.user.UserId;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BioProfileService {

    private final BioProfileRepository repository;

    @Transactional(readOnly = true)
    public Optional<BioProfile> findById(UserId userId) {
        return repository.findById(userId)
                .map(entity -> new BioProfile(entity.getId(), entity.getBio()));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void editProfile(UserId userId, EditedProfile editedProfile) {
        repository.findById(userId)
                .ifPresent(entity -> entity.updateBio(editedProfile.getBio()));
    }
}
