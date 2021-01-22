package ibeere.user.auth.google;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import static org.apache.commons.lang3.StringUtils.endsWith;

@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
public class GoogleUserToken {

    private final String value;

    public static GoogleUserToken of(String id) {
        return new GoogleUserToken(valid(id) ? id : id + "ibeere-google");
    }

    public static boolean valid(String value) {
        return endsWith(value, "ibeere-google");
    }

    public String getOriginal() {
        return StringUtils.replace(value, "ibeere-google", "");
    }

}
