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

    /**
     * Loans a book identified by title (case-insensitive) to the user.
     */
    public Loan loanByTitle(User user, String rawTitle, LocalDate today) {
        Book book = resolveUniqueByTitle(rawTitle);
        return loan(user, book.getIsbn(), today);
    }

    /**
     * Returns a book identified by title (case-insensitive) for the user.
     */
    public void returnByTitle(User user, String rawTitle, LocalDate today) {
        Book book = resolveUniqueByTitle(rawTitle);
        returnBook(user, book.getIsbn(), today);
    }

    private void requireLogin(User user) {
        if (user == null) {
            throw new AuthException("로그인 필요");
        }
    }

    private Book resolveUniqueByTitle(String rawTitle) {
        String normalized = normalize(rawTitle);
        List<Book> exactMatches = bookRepository.findByTitleIgnoreCase(normalized);
        List<Book> candidates = exactMatches.isEmpty()
                ? bookRepository.searchByTitle(normalized)
                : exactMatches;

        if (candidates.isEmpty()) {
            throw new NotFoundException("제목 '" + rawTitle + "'으로 검색된 도서가 없습니다.");
        }
        if (candidates.size() > 1) {
            StringBuilder sb = new StringBuilder("같은/유사한 제목이 여러 권입니다. ISBN으로 다시 시도하세요:\n");
            for (Book book : candidates) {
                sb.append("- ")
                  .append(book.getTitle())
                  .append(" | ")
                  .append(book.getAuthor())
                  .append(" | ISBN: ")
                  .append(book.getIsbn())
                  .append('\n');
            }
            throw new BusinessRuleException(sb.toString());
        }
        return candidates.get(0);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
