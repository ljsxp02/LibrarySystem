package library.domain.loan;

import java.time.Duration;

import library.domain.book.Book;
import library.domain.user.Role;
import library.domain.user.User;

/**
 * Default loan policy implementation.
 */
public class DefaultLoanPolicy implements LoanPolicy {
    private static final Duration MEMBER_DURATION = Duration.ofDays(14);
    private static final Duration ADMIN_DURATION = Duration.ofDays(30);

    @Override
    public Duration loanDuration(User user, Book book) {
        return user.getRole() == Role.ADMIN ? ADMIN_DURATION : MEMBER_DURATION;
    }

    @Override
    public int maxConcurrentLoans(User user) {
        return user.getRole() == Role.ADMIN ? 99 : 5;
    }
}
