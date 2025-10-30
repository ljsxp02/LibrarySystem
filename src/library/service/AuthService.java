package library.service;

import java.util.Objects;

import library.domain.user.Role;
import library.domain.user.User;
import library.exception.AuthException;
import library.exception.ValidationException;
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

    /**
     * Registers a new member account.
     *
     * @param id          desired user id
     * @param rawPassword desired password
     * @param name        display name
     * @return created user
     */
    public User register(String id, String rawPassword, String name) {
        validateRegistrationInput(id, rawPassword, name);
        userRepository.findById(id).ifPresent(existing -> {
            throw new ValidationException("이미 사용 중인 아이디입니다.");
        });

        String passwordHash = PasswordEncoder.encode(rawPassword);
        User user = new User(id, passwordHash, name, Role.MEMBER);
        userRepository.save(user);
        return user;
    }

    private void validateRegistrationInput(String id, String rawPassword, String name) {
        if (id == null || id.isBlank()) {
            throw new ValidationException("아이디는 필수입니다.");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new ValidationException("비밀번호는 필수입니다.");
        }
        if (name == null || name.isBlank()) {
            throw new ValidationException("이름은 필수입니다.");
        }
    }
}
