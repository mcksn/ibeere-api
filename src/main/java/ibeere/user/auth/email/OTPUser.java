package ibeere.user.auth.email;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ibeere.ddd.ValueObject;
import ibeere.user.UserId;

@Getter
@RequiredArgsConstructor
public class OTPUser implements ValueObject {

    private final UserId userId;
    private final OTP otp;
}
