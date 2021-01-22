package ibeere.aggregate.credential;

import ibeere.ddd.ImmutableEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/** common credential characteristics */
public interface Credential extends ImmutableEntity {

    /**
     * generates human readable text of given credentials.
     */
    static String asOnAnswerText(List<Credential> credentials) {
        String text;
        final List<String> credentialsAsText = credentials.stream()
                .sorted(comparing(c -> c.getType().ordinal()))
                .map(Credential::mapToHumanText)
                .collect(toList());

        text = credentialsAsText
                .stream()
                .collect(joining(" | "));

        if (!StringUtils.isBlank(text)) {
            text = " | " + text;
        }

        return text;
    }

    CredentialId getCredentialId();

    CredentialType getType();

    String getText();

    boolean isApplicable();

    String mapToHumanText();
}
