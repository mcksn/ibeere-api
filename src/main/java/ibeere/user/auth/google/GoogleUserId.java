package ibeere.user.auth.google;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

@Embeddable
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
public class GoogleUserId implements Serializable {

    private String id;

    public static GoogleUserId of(String id) {
        return new GoogleUserId(valid(id) ? id : id + "ibeere-google");
    }

    public static boolean valid(String value) {
        return StringUtils.endsWith(value, "ibeere-google");
    }

    @Column(name = "google_user_id")
    public String getId() {
        return id;
    }
}
