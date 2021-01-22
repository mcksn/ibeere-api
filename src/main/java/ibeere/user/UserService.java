package ibeere.user;

import com.google.api.client.util.Value;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Template;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;
import ibeere.aggregate.profile.micro.FullName;
import ibeere.aggregate.profile.ProfileService;
import ibeere.aggregate.question.QuestionId;
import ibeere.support.ClockProvider;
import ibeere.user.auth.AuthType;
import ibeere.user.auth.TemporaryUser;
import ibeere.user.auth.email.MagicLinkEmailGenerator;
import ibeere.user.auth.email.OTPUser;
import ibeere.user.auth.google.GoogleUserId;
import ibeere.user.auth.twitter.*;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import java.time.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.time.Instant.now;
import static java.util.Optional.*;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.StringUtils.*;
import static org.springframework.http.HttpStatus.SEE_OTHER;

/**
 * User aggregate is a mess. This service has too many responsibilities!
 * Mix of registered user behaviours and authenticating/authenticated user behaviours.
 */
@Service
@Transactional
public class UserService {
    private static final Integer EXPIRE_MINS = 15;
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    static {
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.client.protocol.ResponseProcessCookies", "fatal");
    }

    @Autowired
    private UserRepository repository;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private MagicLinkEmailGenerator magicLinkEmailGenerator;
    @Autowired
    private ClockProvider clockProvider;
    private LoadingCache<String, TemporaryUser> otpCache;
    private LoadingCache<TwitterUserId, TemporaryUser> twitterAccessCache;
    private LoadingCache<TwitterRequestToken, TemporaryUser> twitterRequestCache;
    @Value("${twitter.consumerKey:")
    private String consumerKey;
    @Value("${twitter.consumerSecret:")
    private String consumerSecret;

    @PostConstruct
    public void init() {
        otpCache = CacheBuilder.newBuilder().
                expireAfterWrite(EXPIRE_MINS, TimeUnit.MINUTES).build(new CacheLoader<String, TemporaryUser>() {
            public TemporaryUser load(String key) {
                return null; //should never need to load, always putting first
            }
        });
        twitterAccessCache = CacheBuilder.newBuilder().
                expireAfterWrite(EXPIRE_MINS, TimeUnit.MINUTES).build(new CacheLoader<TwitterUserId, TemporaryUser>() {
            public TemporaryUser load(TwitterUserId key) {
                return null; //should never need to load, always putting first
            }
        });
        twitterRequestCache = CacheBuilder.newBuilder().
                expireAfterWrite(EXPIRE_MINS, TimeUnit.MINUTES).build(new CacheLoader<TwitterRequestToken, TemporaryUser>() {
            public TemporaryUser load(TwitterRequestToken key) {
                return null; //should never need to load, always putting first
            }
        });
    }

    @Transactional(readOnly = true)
    public AuthType whichAuthType(String email) {
        return repository.findByEmail(email).get().authType();
    }

    @Validated
    public Long newEmailOtpForNewUser(String email, String firstName, String lastName, String bio) throws UserInputException {

        if (isBlank(email)) {
            throw new UserInputException("Email is empty");
        }

        String emailTrimmed = email.toLowerCase().trim();

        if (repository.findByEmail(emailTrimmed).isPresent()) {
            throw new UserInputException("This email has already been registered. Try to login.");
        }

        final FullName fullName = FullNameValidator.toFullName(firstName, lastName);

        UserId personId = UserId.of(randomUUID());
        return generateAndCacheOTP(personId, fullName, emailTrimmed, bio, "password-tmp");
    }

    public Long newOtpForExistingUser(String email) {
        String trimmedEmail = email.toLowerCase().trim();
        final UserEntity byEmail = repository.findByEmail(trimmedEmail)
                .orElseThrow(() -> new ResponseStatusException(SEE_OTHER, "User not found"));
        return generateAndCacheOTP(byEmail.getId(), FullName.of(byEmail.getName(), byEmail.getLastName()), byEmail.getEmail(), byEmail.getBio(), "password-tmp");
    }

    public Optional<OTPUser> findOtp(String email) {
        final TemporaryUser tempUser = otpCache.getIfPresent(email.toLowerCase().trim());
        return ofNullable(tempUser).map(a -> new OTPUser(tempUser.getUserId(), a.getOtp()));
    }

    public Optional<TwitterAccessTokenUser> findAccessToken(TwitterUserId twitterUserId, TwitterAccessToken twitterAccessToken) {
        final TemporaryUser tempUser = twitterAccessCache.getIfPresent(twitterUserId);
        return ofNullable(tempUser)
                .map(a -> new TwitterAccessTokenUser(tempUser.getUserId(), a.getTwitterAccessToken()));
    }

    public TwitterRequestToken initialTwitterSignUpStage(String firstName, String lastName, String callbackUrl) throws UserInputException {

        final OAuthToken oAuthToken = new OAuth1Template(consumerKey, consumerSecret,
                "https://api.twitter.com/oauth/request_token",
                "", "", "https://api.twitter.com/oauth/access_token")
                .fetchRequestToken(callbackUrl, null);
        TemporaryUser tempUser = new TemporaryUser(UserId.of(randomUUID()), FullNameValidator.toFullName(firstName, lastName), null, null, null, null, null, TwitterRequestTokenSecret.of(oAuthToken.getSecret()));
        final TwitterRequestToken twitterRequestToken = TwitterRequestToken.of(oAuthToken.getValue());
        twitterRequestCache.put(twitterRequestToken, tempUser);
        return twitterRequestToken;
    }

    public TwitterRequestToken initialTwitterLoginStage(String callbackUrl) {
        final OAuthToken oAuthToken = new OAuth1Template(consumerKey, consumerSecret,
                "https://api.twitter.com/oauth/request_token",
                "", "", "https://api.twitter.com/oauth/access_token")
                .fetchRequestToken(callbackUrl, null);
        TemporaryUser tempUser = new TemporaryUser(null, null, null, null, null, null, null, TwitterRequestTokenSecret.of(oAuthToken.getSecret()));
        final TwitterRequestToken twitterRequestToken = TwitterRequestToken.of(oAuthToken.getValue());
        twitterRequestCache.put(twitterRequestToken, tempUser);
        return twitterRequestToken;
    }

    public Optional<TwitterIdAndAccessToken> keepAccessCodeFromSignUp(TwitterRequestToken twitterRequestToken, String verifier) throws TwitterUserAlreadyExistsException {
        final TemporaryUser tempUser = twitterRequestCache.getIfPresent(twitterRequestToken);


        if (tempUser != null) {
            final OAuthToken oAuthToken = new OAuth1Template(consumerKey, consumerSecret,
                    "https://api.twitter.com/oauth/request_token",
                    "", "", "https://api.twitter.com/oauth/access_token")
                    .exchangeForAccessToken(new AuthorizedRequestToken(new OAuthToken(twitterRequestToken.getValue(), tempUser.getTwitterRequestTokenSecret().getSecret()), verifier), null);

            final TwitterAccessToken twitterAccessToken = TwitterAccessToken.of(oAuthToken.getValue(), oAuthToken.getSecret());
            tempUser.updateTwitterAccessToken(twitterAccessToken);

            TwitterTemplate twitterTemplate =
                    new TwitterTemplate(consumerKey, consumerSecret, twitterAccessToken.getValue(), twitterAccessToken.getSecret());

            final String profileImageUrl = twitterTemplate.userOperations().getUserProfile().getProfileImageUrl();
            final String twitterBio = twitterTemplate.userOperations().getUserProfile().getDescription();
            final long twitterId = twitterTemplate.userOperations().getUserProfile().getId();
            final String twitterEmail = twitterTemplate.getRestTemplate().getForObject("https://api.twitter.com/1.1/account/verify_credentials.json?include_email=true", String.class);
            TwitterUserId twitterUserId = TwitterUserId.of(twitterId);

            TwitterIdAndAccessToken twitterIdAndAccessToken = TwitterIdAndAccessToken.builder()
                    .password(tempUser.getTwitterAccessToken().serialized())
                    .twitterId(twitterUserId.getId().toString())
                    .build();

            if (this.repository.findByTwitterUserId(twitterUserId).isPresent()) {
                throw new TwitterUserAlreadyExistsException();
            }

            final UserEntity saved = this.repository.save(
                    new UserEntity(tempUser.getUserId(), tempUser.getFullName(), null, tempUser.getEmail(), tempUser.getTwitterAccessToken().serialized(), null, twitterUserId, null,
                            Instant.now(Clock.system(ZoneId.of("UTC+1")))));
            profileService.newProfile(new TemporaryUser(tempUser.getUserId(), tempUser.getFullName(), twitterBio, twitterEmail, null, profileImageUrl, tempUser.getTwitterAccessToken(), null));

            LOG.info("Creating user during sign up. About to flush. " + saved);

            twitterAccessCache.put(twitterUserId, tempUser);

            return Optional.of(twitterIdAndAccessToken);
        } else {
            return empty();
        }
    }

    public Optional<TwitterIdAndAccessToken> keepAccessCodeFromLogin(TwitterRequestToken twitterRequestToken, String verifier) throws TwitterUserDoesNotExistException {
        final TemporaryUser tempUser = twitterRequestCache.getIfPresent(twitterRequestToken);

        if (tempUser != null) {
            final OAuthToken oAuthToken = new OAuth1Template(consumerKey, consumerSecret,
                    "https://api.twitter.com/oauth/request_token",
                    "", "", "https://api.twitter.com/oauth/access_token")
                    .exchangeForAccessToken(new AuthorizedRequestToken(new OAuthToken(twitterRequestToken.getValue(), tempUser.getTwitterRequestTokenSecret().getSecret()), verifier), null);

            final TwitterAccessToken twitterAccessToken = TwitterAccessToken.of(oAuthToken.getValue(), oAuthToken.getSecret());
            tempUser.updateTwitterAccessToken(twitterAccessToken);

            TwitterTemplate twitterTemplate =
                    new TwitterTemplate(consumerKey, consumerSecret, twitterAccessToken.getValue(), twitterAccessToken.getSecret());

            final long twitterId = twitterTemplate.userOperations().getUserProfile().getId();
            TwitterUserId twitterUserId = TwitterUserId.of(twitterId);

            TwitterIdAndAccessToken twitterIdAndAccessToken = TwitterIdAndAccessToken.builder()
                    .password(tempUser.getTwitterAccessToken().serialized())
                    .twitterId(twitterUserId.getId().toString())
                    .build();

            final Optional<UserEntity> byTwitterUserId = this.repository.findByTwitterUserId(twitterUserId);
            if (!byTwitterUserId.isPresent()) {
                throw new TwitterUserDoesNotExistException();
            }

            byTwitterUserId.get().updatePassword(tempUser.getTwitterAccessToken().serialized());

            LOG.info("Updating user during login. About to flush. " + byTwitterUserId.get());

            twitterAccessCache.put(twitterUserId, tempUser);

            return Optional.of(twitterIdAndAccessToken);
        } else {
            return empty();
        }
    }

    @Deprecated
    public Optional<TwitterIdAndAccessToken> keepAccessCode(TwitterRequestToken twitterRequestToken, String verifier) {
        final TemporaryUser tempUser = twitterRequestCache.getIfPresent(twitterRequestToken);


        if (tempUser != null) {
            final OAuthToken oAuthToken = new OAuth1Template(consumerKey, consumerSecret,
                    "https://api.twitter.com/oauth/request_token",
                    "", "", "https://api.twitter.com/oauth/access_token")
                    .exchangeForAccessToken(new AuthorizedRequestToken(new OAuthToken(twitterRequestToken.getValue(), tempUser.getTwitterRequestTokenSecret().getSecret()), verifier), null);

            final TwitterAccessToken twitterAccessToken = TwitterAccessToken.of(oAuthToken.getValue(), oAuthToken.getSecret());
            tempUser.updateTwitterAccessToken(twitterAccessToken);

            TwitterTemplate twitterTemplate =
                    new TwitterTemplate(consumerKey, consumerSecret, twitterAccessToken.getValue(), twitterAccessToken.getSecret());

            final String profileImageUrl = twitterTemplate.userOperations().getUserProfile().getProfileImageUrl();
            final String twitterBio = twitterTemplate.userOperations().getUserProfile().getDescription();
            final long twitterId = twitterTemplate.userOperations().getUserProfile().getId();
            final String twitterEmail = twitterTemplate.getRestTemplate().getForObject("https://api.twitter.com/1.1/account/verify_credentials.json?include_email=true", String.class);
            TwitterUserId twitterUserId = TwitterUserId.of(twitterId);

            twitterAccessCache.put(twitterUserId, tempUser);

            final TwitterIdAndAccessToken twitterIdAndAccessToken =
                    TwitterIdAndAccessToken.builder()
                            .password(tempUser.getTwitterAccessToken().serialized())
                    .twitterId(twitterUserId.getId().toString())
                    .build();

            this.repository.findByTwitterUserId(twitterUserId)
                    .map(e -> {
                        e.updatePassword(tempUser.getTwitterAccessToken().serialized());
                        return e;
                    })
                    .orElseGet(() -> {
                        final UserEntity entity = this.repository.save(
                                new UserEntity(tempUser.getUserId(), tempUser.getFullName(),
                                        null,
                                        tempUser.getEmail(),
                                        tempUser.getTwitterAccessToken().serialized(),
                                        null,
                                        twitterUserId,
                                        null,
                                        Instant.now(clockProvider.standardClock())));
                        LOG.info("Creating user. About to flush. " + entity);
                        profileService.newProfile(new TemporaryUser(tempUser.getUserId(), tempUser.getFullName(), twitterBio, twitterEmail, null, profileImageUrl, tempUser.getTwitterAccessToken(), null));
                        return entity;
                    });

            return Optional.of(twitterIdAndAccessToken);
        } else {
            return empty();
        }
    }

    @Transactional(readOnly = true)
    public Optional<User> findByGoogleUserId(GoogleUserId googleUserId) {
        return repository.findByGoogleUserId(googleUserId)
                .map(entity -> map(entity, null));
    }

    public User newUser(GoogleUserId googleUserId, String firstName, String lastName, String email, String profileImgUrl) throws UserAlreadyRegisteredException {

        Optional<UserEntity> foundMaybe = repository.findByEmail(email);

        if (foundMaybe.isPresent()) {
            throw new UserAlreadyRegisteredException(foundMaybe.get().getEmail());
        }

        FullName fullName = FullName.of(firstName, lastName);
        final UserEntity entity = new UserEntity(UserId.of(UUID.randomUUID()),
                fullName,
                null,
                email,
                "password-tmp",
                null,
                null,
                googleUserId,
                Instant.now(Clock.system(ZoneId.of("UTC+1"))));
        repository.save(entity);

        TemporaryUser tempUser = new TemporaryUser(entity.getId(), fullName, null,
                entity.getEmail(), null, profileImgUrl, null, null);

        profileService.newProfile(tempUser);
        return map(entity, null);
    }

    public Optional<CookieUser> generateUserNameAndPassword(String email) {

        final Optional<CookieUser> existingCookieUser = repository.findByEmail(ofNullable(email)
                .map(String::toLowerCase)
                .map(String::trim).get())
                .map(u -> new CookieUser(u.getId(), u.getPassword()));

        if (existingCookieUser.isPresent())
            return existingCookieUser;

        final Optional<TemporaryUser> tempUserOpt = ofNullable(email)
                .map(String::toLowerCase)
                .map(String::trim)
                .flatMap(trimmed -> ofNullable(otpCache.getIfPresent(trimmed)));

        return tempUserOpt.map(tempUser -> {
            final UserEntity entity = new UserEntity(tempUser.getUserId(),
                    tempUser.getFullName(),
                    tempUser.getBio(),
                    tempUser.getEmail(),
                    "password-tmp",
                    null,
                    null,
                    null,
                    Instant.now(clockProvider.standardClock()));
            repository.save(entity);
            profileService.newProfile(tempUser);
            return entity;
        })
                .map(userEntity -> new CookieUser(userEntity.getId(), userEntity.getPassword()));

    }

    public Optional<CookieUser> findCookieSecretByTwitterUserId(TwitterUserId twitterUserId) {
        return repository.findByTwitterUserId(twitterUserId)
                .map(userEntity -> new CookieUser(userEntity.getId(), userEntity.getPassword()));
    }

    public Optional<CookieUser> findCookieSecretByGoogleUserId(GoogleUserId googleUserId) {
        return repository.findByGoogleUserId(googleUserId)
                .map(userEntity -> new CookieUser(userEntity.getId(), userEntity.getPassword()));
    }

    public List<String> findValidEmailsByIds(Collection<UserId> userIds) {
        return repository.findAllById(userIds).stream()
                .map(UserEntity::getEmail)
                .filter(email -> !endsWith(email, "email")) //probably a fake account we created
                .filter(email -> !isBlank(email))
                .collect(Collectors.toList());
    }

    public void recordEmailSent(String email) {
        repository.findByEmail(email)
                .ifPresent(user -> user.recordEmailSent(clockProvider.standardClock()));
    }

    public List<UserId> findAllById() {
        return repository.findAllId();
    }

    @Transactional(readOnly = true)
    public User findById(UserId userId, QuestionId recentQuestionIdAsked) {
        return repository.findById(userId).map(userEntity -> {
            userEntity.initLazyCollections();

            final User user = map(userEntity, recentQuestionIdAsked);
            return user;
        })
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public User findById(UserId userId) {
        return findById(userId, null);
    }

    // there is a second call coming in from some where I believe which invokes email generation to run twice, maybe
    // because the otp cache is not populated when the other request does the getIfPresent
    private synchronized Long generateAndCacheOTP(UserId userId, FullName fullName, String email, String bio, String password) {
        final TemporaryUser tempUser = otpCache.getIfPresent(email);
        if (tempUser == null) {
            final TemporaryUser otpTempUser = new TemporaryUser(userId, fullName, bio, email, magicLinkEmailGenerator.generateMagicLinkEmail(email, fullName), null, null, null);
            otpCache.put(email, otpTempUser);
            return otpTempUser.getCreatedTimestamp();
        } else {
            return tempUser.getCreatedTimestamp();
        }
    }

    private User map(UserEntity entity, QuestionId recentQuestionIdAsked) {
        return new User(
                entity.getId(),
                FullName.of(entity.getName(), entity.getLastName()),
                entity.getBio(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getFollows(),
                entity.getUpVotes(),
                entity.getDownVotes(),
                entity.getSignUpDate(),
                recentQuestionIdAsked);
    }
}