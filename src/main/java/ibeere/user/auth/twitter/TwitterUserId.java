package ibeere.user.auth.twitter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import ibeere.framework.IdentifierDeConverter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

@Embeddable
@JsonDeserialize(converter = IdentifierDeConverter.TwitterUserId.class)
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode
@Getter
@NoArgsConstructor(access = PRIVATE)
public class TwitterUserId implements Serializable {

    @Column(name = "twitter_user_id")
    private Long id;
}
