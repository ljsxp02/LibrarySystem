package library.repo;

import java.util.Collection;
import java.util.Optional;

import library.domain.user.User;

/**
 * Repository for accessing users.
 */
public interface UserRepository {
    Optional<User> findById(String id);

    void save(User user);

    Collection<User> findAll();
}
