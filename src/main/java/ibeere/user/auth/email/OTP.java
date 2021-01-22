package ibeere.user.auth.email;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ibeere.ddd.ValueObject;

import java.util.Objects;
import java.util.Random;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class OTP implements ValueObject {
    private final String value;

    public static OTP newInstance() {
        Random random = new Random();
        int otpValue = 100000 + random.nextInt(900000);
        return new OTP(String.valueOf(otpValue));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OTP that = (OTP) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
