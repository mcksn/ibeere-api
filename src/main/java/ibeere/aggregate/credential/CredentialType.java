package ibeere.aggregate.credential;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;
import java.util.function.Predicate;

import static org.apache.logging.log4j.util.Strings.isBlank;
import static ibeere.support.Utils.getCountsToHuman;

/**
 * Type of credential and supported.
 * Includes functions specific to the type.
 */
@Getter
@RequiredArgsConstructor
public enum CredentialType {
    CUSTOM("", true,
            text -> text,
            text -> !isBlank(text)),
    WORKING_AT("", true,
            text -> "Working at " + text,
            text -> !isBlank(text)),
    WORKED_AT("", true,
            text -> "Worked at " + text,
            text -> !isBlank(text)),
    WORKS_AS_A("", true,
            text -> text,
            text -> !isBlank(text)),
    STUDIED("", true,
            text -> "Studied " + text,
            text -> !isBlank(text)),
    STUDYING("", true,
            text -> "Studying " + text,
            text -> !isBlank(text)),
    STUDIED_AT("", true,
            text -> "Studied at " + text,
            text -> !isBlank(text)),
    STUDYING_AT("", true,
            text -> "Studying at " + text,
            text -> !isBlank(text)),
    LIVED_AT("", true,
            text -> "Lived at " + text,
            text -> !isBlank(text)),
    LIVING_IN("", true,
            text -> "Living in " + text,
            text -> !isBlank(text)),
    KNOWS_ABOUT("", true,
            text -> "Knows about " + text,
            text -> !isBlank(text)),
    ANSWERED("0", false,
            text -> Long.parseLong(text) > 1L ? "Answered " + text + " others" : "Answered" + text + " or more",
            text -> Long.parseLong(text) > 2L),
    VIEWS("0", false,
            (text) -> getCountsToHuman(Long.parseLong(text)) + " content views",
            (text) -> Long.parseLong(text) > 100L);

    private final String defaultText;
    private final boolean userGenerated;
    private final Function<String, String> toHumanTextMapper;
    private final Predicate<String> applicabilityPredicate;
}
