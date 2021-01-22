package ibeere.ddd;

/**
 * Entity within an aggregate.
 * Identifiable within the context of the aggregate.
 * Mutable.
 */
public interface Entity<T> {

    T getId();
}
