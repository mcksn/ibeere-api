package ibeere.aggregate.profile.micro;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ibeere.aggregate.profile.*;
import ibeere.user.auth.TemporaryUser;
import ibeere.user.UserId;
import ibeere.user.UserInputException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service("uncachedMicroProfileService")
@Transactional
@RequiredArgsConstructor
public class MicroProfileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MicroProfileService.class);

    protected final MicroProfileRepository repository;

    @Transactional(readOnly = true)
    public boolean exists(UserId userId) {
        return repository.existsById(userId);
    }

    @Transactional(readOnly = true)
    public Optional<MicroProfile> findMicroBy(UserId userId) {
        return repository.findById(userId)
                .map(entity -> new MicroProfile(entity.getId(), entity.getName(), entity.getImgUrl(), entity.getPath(), entity.getQandAStatus(), entity.isVerified()));
    }

    @Transactional(readOnly = true)
    public List<MicroProfile> findMicroBy(Set<UserId> userIds) {
        return repository.findAllById(userIds).stream()
                .map(entity -> new MicroProfile(entity.getId(), entity.getName(), entity.getImgUrl(), entity.getPath(), entity.getQandAStatus(), entity.isVerified()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MicroProfile> findAllMicro() {
        return repository.findAll().stream()
                .map(entity -> new MicroProfile(entity.getId(), entity.getName(), entity.getImgUrl(), entity.getPath(), entity.getQandAStatus(), entity.isVerified()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<MicroProfile> findMicroByPath(String path) {
        return repository.findByPath(path)
                .map(entity -> new MicroProfile(entity.getId(), entity.getName(), entity.getImgUrl(), entity.getPath(), entity.getQandAStatus(), entity.isVerified()));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<String> editProfile(UserId userId, EditedProfile editedProfile) throws UserInputException {

        String lastNameTrimmed = editedProfile.getFullName().last().trim();
        String firstNameTrimmed = editedProfile.getFullName().first().trim();

        if (lastNameTrimmed.length() > 20 || firstNameTrimmed.length() > 20) {
            throw new UserInputException("Name is too long");
        }

        if (isBlank(firstNameTrimmed)) {
            throw new UserInputException("No first name given");
        }

        if (isBlank(lastNameTrimmed)) {
            throw new UserInputException("No last name given");
        }

        final Optional<MicroProfileEntity> profileEntity = repository.findById(userId);

        return profileEntity.map(e -> {
            e.updateName(FullName.of(firstNameTrimmed, lastNameTrimmed));
            return e.getPath();
        });
    }

    @Transactional(propagation = REQUIRES_NEW)
    public void newProfile(TemporaryUser user) {

        if (repository.existsById(user.getUserId())) {
            LOGGER.warn("Attempt to add profile {} when already added", user.getUserId());
        }
        final MicroProfileEntity entity = new MicroProfileEntity(user.getUserId(), user.getImgUrl(), user.getFullName());

        repository.save(entity);
    }

    public void changeImg(UserId userId, String imgUrl) {
        if (!imgUrl.startsWith("https://res.cloudinary.com/mcksn/image/upload") &&
                !imgUrl.startsWith("http://res.cloudinary.com/mcksn/image/upload")) {
            LOGGER.warn("Attempt to upload img not from cloudinary {} {}", userId, imgUrl);
        }
        repository.findById(userId)
                .ifPresent(profileEntity1 -> profileEntity1.changeImgUrl(imgUrl));
    }
}
