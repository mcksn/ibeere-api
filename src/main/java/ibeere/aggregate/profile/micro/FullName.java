package ibeere.aggregate.profile.micro;

import lombok.RequiredArgsConstructor;
import ibeere.ddd.ValueObject;

@RequiredArgsConstructor(staticName = "of")
public class FullName implements ValueObject {
    private String first;
    private String last;

    public static FullName of(String first, String last) {
        final FullName fullName = new FullName();
        fullName.first = first;
        fullName.last = last;
        return fullName;
    }

    public String first() {
        return first;
    }

    public String last() {
        return last;
    }

    public String full() {

        final StringBuilder builder = new StringBuilder()
                .append(first);

        if (last != null  && !last.isEmpty()) {
            builder.append(" ");
            builder.append(last);
        }

        return builder.toString();
    }
}
