package library.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import library.domain.book.Book;
import library.domain.loan.Loan;
import library.domain.loan.LoanPolicy;
import library.domain.user.User;
import library.exception.AuthException;
import library.exception.BusinessRuleException;
import library.exception.NotFoundException;
import library.repo.BookRepository;
import library.repo.LoanRepository;

/**
 * Service handling loan and return operations.
 */
public class LoanService {
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final LoanPolicy loanPolicy;

    public LoanService(BookRepository bookRepository, LoanRepository loanRepository, LoanPolicy loanPolicy) {
        this.bookRepository = Objects.requireNonNull(bookRepository, "bookRepository");
        this.loanRepository = Objects.requireNonNull(loanRepository, "loanRepository");
        this.loanPolicy = Objects.requireNonNull(loanPolicy, "loanPolicy");
    }

    /**
     * Loans a book to the user.
     */
    public Loan loan(User user, String isbn, LocalDate today) {
        requireLogin(user);
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new NotFoundException("도서를 찾을 수 없습니다"));
        if (book.getAvailable() <= 0) {
            throw new BusinessRuleException("재고 부족");
        }
        List<Loan> activeLoans = loanRepository.findActiveByUser(user.getId());
        int max = loanPolicy.maxConcurrentLoans(user);
        if (activeLoans.size() >= max) {
            throw new BusinessRuleException("대출 가능 권수를 초과했습니다");
        }
        loanRepository.findActiveByUserAndIsbn(user.getId(), isbn).ifPresent(existing -> {
            throw new BusinessRuleException("이미 대출 중인 도서입니다");
        });
        book.takeOne();
        bookRepository.save(book);
        long days = loanPolicy.loanDuration(user, book).toDays();
        if (days <= 0) {
            throw new BusinessRuleException("대출 기간이 유효하지 않습니다");
        }
        LocalDate dueDate = today.plusDays(days);
        Loan loan = new Loan(user.getId(), isbn, today, dueDate);
        loanRepository.save(loan);
        return loan;
    }

    /**
     * Returns a book previously loaned by the user.
     */
    public void returnBook(User user, String isbn, LocalDate today) {
        requireLogin(user);
        Loan loan = loanRepository.findActiveByUserAndIsbn(user.getId(), isbn)
                .orElseThrow(() -> new NotFoundException("해당 사용자 미반납 대출 없음"));
        loan.setReturnedAt(today);
        loanRepository.save(loan);
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new NotFoundException("도서를 찾을 수 없습니다"));
        book.returnOne();
        bookRepository.save(book);
    }

    private void requireLogin(User user) {
        if (user == null) {
            throw new AuthException("로그인 필요");
        }
    }
}
