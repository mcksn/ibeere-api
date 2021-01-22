package ibeere.user.auth;

import lombok.Getter;
import ibeere.aggregate.profile.micro.FullName;
import ibeere.user.UserId;
import ibeere.user.auth.email.OTP;
import ibeere.user.auth.twitter.TwitterAccessToken;
import ibeere.user.auth.twitter.TwitterRequestTokenSecret;

import java.time.ZonedDateTime;

import static ibeere.support.Constants.STANDARD_ZONE;

@Getter
public class TemporaryUser {
    private final UserId userId;
    private final FullName fullName;
    private final String bio;
    private final String email;
    private final String imgUrl;
    private final OTP otp;
    private final TwitterRequestTokenSecret twitterRequestTokenSecret;
    private final Long createdTimestamp;

    private TwitterAccessToken twitterAccessToken;

    public TemporaryUser(UserId userId, FullName fullName, String bio, String email, OTP otp, String imgUrl, TwitterAccessToken twitterAccessToken, TwitterRequestTokenSecret twitterRequestTokenSecret) {
        this.userId = userId;
        this.fullName = fullName;
        this.bio = bio;
        this.email = email;
        this.otp = otp;
        this.imgUrl = imgUrl;
        this.twitterAccessToken = twitterAccessToken;
        this.twitterRequestTokenSecret = twitterRequestTokenSecret;
        this.createdTimestamp = ZonedDateTime.now(STANDARD_ZONE).toEpochSecond();
    }

    public void updateTwitterAccessToken(TwitterAccessToken token) {
        twitterAccessToken = token;
    }
}
