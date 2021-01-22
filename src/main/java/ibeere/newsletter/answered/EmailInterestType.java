package ibeere.newsletter.answered;

import lombok.RequiredArgsConstructor;

/** Tells us why a user is getting an email */
@RequiredArgsConstructor
public enum EmailInterestType {
    FOLLOWED("followed"), ASKED("asked");

    private final String text;

    public String text() {
        return text;
    }
}
