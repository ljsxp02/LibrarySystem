package library.service;

import java.util.Objects;

import library.domain.user.User;
import library.exception.AuthException;
import library.repo.UserRepository;
import library.util.PasswordEncoder;

/**
 * Handles user authentication.
 */
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository");
    }

    /**
     * Authenticates the user by id and password.
     *
     * @param id          user identifier
     * @param rawPassword password provided by the user
     * @return authenticated user
     */
    public User login(String id, String rawPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AuthException("아이디/비밀번호 불일치"));
        if (!PasswordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new AuthException("아이디/비밀번호 불일치");
        }
        return user;
    }
}
