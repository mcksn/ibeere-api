package ibeere.aggregate.profile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ibeere.ddd.ValueObject;
import ibeere.aggregate.profile.micro.FullName;

@RequiredArgsConstructor
@Getter
public class EditedProfile implements ValueObject {
    private final String bio;
    private final FullName fullName;
}
