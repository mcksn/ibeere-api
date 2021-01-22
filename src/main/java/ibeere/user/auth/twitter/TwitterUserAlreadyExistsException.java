package ibeere.user.auth.twitter;

public class TwitterUserAlreadyExistsException extends Exception {
    public TwitterUserAlreadyExistsException() {
        super("Already signed up. Try \"Login with Twitter\"");
    }
}
