package ibeere.user.auth.twitter;

import ibeere.user.UserId;

public class TwitterAccessTokenUser {

    private UserId userId;
    private TwitterAccessToken twitterAccessToken;

    public TwitterAccessTokenUser(UserId userId, TwitterAccessToken twitterAccessToken) {
        this.userId = userId;
        this.twitterAccessToken = twitterAccessToken;
    }

    public UserId getUserId() {
        return userId;
    }

    public TwitterAccessToken getTwitterAccessToken() {
        return twitterAccessToken;
    }
}
