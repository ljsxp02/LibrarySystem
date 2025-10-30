package library.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import library.domain.book.Book;
import library.domain.loan.Loan;
import library.domain.user.User;
import library.exception.AuthException;
import library.exception.NotFoundException;
import library.repo.BookRepository;
import library.repo.LoanRepository;
import library.repo.UserRepository;

/**
 * Service for administrator overdue inquiries.
 */
public class OverdueService {
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public OverdueService(LoanRepository loanRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.loanRepository = Objects.requireNonNull(loanRepository, "loanRepository");
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository");
        this.bookRepository = Objects.requireNonNull(bookRepository, "bookRepository");
    }

    /**
     * Lists overdue entries for administrator review.
     */
    public List<OverdueEntry> listOverdues(User requester, LocalDate today) {
        requireAdmin(requester);
        List<OverdueEntry> result = new ArrayList<>();
        for (Loan loan : loanRepository.findActive()) {
            if (loan.isOverdue(today)) {
                User user = userRepository.findById(loan.getUserId())
                        .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다"));
                Book book = bookRepository.findByIsbn(loan.getIsbn())
                        .orElseThrow(() -> new NotFoundException("도서를 찾을 수 없습니다"));
                long overdueDays = ChronoUnit.DAYS.between(loan.getDueDate(), today);
                result.add(new OverdueEntry(user, book, loan.getDueDate(), (int) overdueDays));
            }
        }
        return result;
    }

    private void requireAdmin(User requester) {
        if (requester == null || !requester.isAdmin()) {
            throw new AuthException("관리자 권한 필요");
        }
    }

    /**
     * Overdue record details.
     */
    public static record OverdueEntry(User user, Book book, LocalDate dueDate, int overdueDays) {
    }
}
