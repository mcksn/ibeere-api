package ibeere.ddd;

/**
 * Root Entity is the only member of the aggregate that any object outside the aggregate is allowed to hold a reference to.
 */
public interface RootEntity<T> extends Entity<T> {
}