package ibeere.user.auth.twitter;

public class TwitterUserDoesNotExistException extends Exception {
    public TwitterUserDoesNotExistException() {
        super("Not signed up. Try \"Sign Up with Twitter\"");
    }
}
