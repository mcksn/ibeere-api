package ibeere.ddd;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
@JsonSerialize(converter = Identifier.IdentifierConverter.class)
@Setter //Setters here are probably not the best thing for immutability. Refactor
public class Identifier implements Serializable {

    @Getter
    private UUID id;

    private transient String note;

    public Identifier() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identifier that = (Identifier) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        final com.google.common.base.Objects.ToStringHelper toStringHelper = com.google.common.base.Objects.toStringHelper(this)
                .addValue(id.toString());

        if (note != null) {
            toStringHelper.addValue(note);
        }

        return toStringHelper
                .toString();
    }

    public static class IdentifierConverter<T extends Identifier> extends StdConverter<T, String> {
        @Override
        public String convert(T value) {
            return value.getId().toString();
        }
    }

    public abstract static class IdentifierDeConverter<T extends Identifier> extends StdConverter<String, T> {

        public IdentifierDeConverter() {
        }

        @Override
        public abstract T convert(String value);
    }
}