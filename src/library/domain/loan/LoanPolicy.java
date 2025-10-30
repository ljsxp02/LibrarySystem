package library.domain.loan;

import java.time.Duration;
import java.time.LocalDate;

import library.domain.book.Book;
import library.domain.user.User;

/**
 * Policy for controlling loan durations and limits.
 */
public interface LoanPolicy {

    /**
     * Calculates the permitted loan duration for the given user and book.
     *
     * @param user user requesting the loan
     * @param book book being loaned
     * @return loan duration
     */
    Duration loanDuration(User user, Book book);

    /**
     * Determines the maximum number of concurrent active loans permitted for the user.
     *
     * @param user user requesting the loan
     * @return maximum concurrent loans
     */
    int maxConcurrentLoans(User user);

    /**
     * Determines if the provided due date is overdue relative to the given date.
     *
     * @param dueDate due date
     * @param today   current date
     * @return {@code true} if overdue
     */
    default boolean isOverdue(LocalDate dueDate, LocalDate today) {
        return today.isAfter(dueDate);
    }
}
