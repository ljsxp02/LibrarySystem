package library.repo;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import library.domain.user.User;

/**
 * In-memory implementation of {@link UserRepository}.
 */
public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> usersById = new ConcurrentHashMap<>();

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(usersById.get(id));
    }

    @Override
    public void save(User user) {
        usersById.put(user.getId(), user);
    }

    @Override
    public Collection<User> findAll() {
        return usersById.values();
    }
}
