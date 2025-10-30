package library.domain.user;

import java.util.Objects;

/**
 * Represents a user in the library system.
 */
public class User {
    private final String id;
    private final String passwordHash;
    private final String name;
    private final Role role;

    /**
     * Creates a new user instance.
     *
     * @param id           unique login identifier
     * @param passwordHash hashed password
     * @param name         display name
     * @param role         role within the system
     */
    public User(String id, String passwordHash, String name, Role role) {
        this.id = Objects.requireNonNull(id, "id");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash");
        this.name = Objects.requireNonNull(name, "name");
        this.role = Objects.requireNonNull(role, "role");
    }

    public String getId() {
        return id;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    /**
     * @return {@code true} if the user has administrator privileges.
     */
    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
