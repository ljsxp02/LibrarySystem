package library.app;

import java.util.Objects;

import library.repo.BookRepository;
import library.repo.LoanRepository;
import library.repo.UserRepository;
import library.service.AuthService;
import library.service.BookService;
import library.service.LoanService;
import library.service.OverdueService;

/**
 * Simple holder for repositories and services used by the console application.
 */
public final class ApplicationContext {
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final BookService bookService;
    private final LoanService loanService;
    private final OverdueService overdueService;

    public ApplicationContext(
            BookRepository bookRepository,
            LoanRepository loanRepository,
            UserRepository userRepository,
            AuthService authService,
            BookService bookService,
            LoanService loanService,
            OverdueService overdueService) {
        this.bookRepository = Objects.requireNonNull(bookRepository, "bookRepository");
        this.loanRepository = Objects.requireNonNull(loanRepository, "loanRepository");
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository");
        this.authService = Objects.requireNonNull(authService, "authService");
        this.bookService = Objects.requireNonNull(bookService, "bookService");
        this.loanService = Objects.requireNonNull(loanService, "loanService");
        this.overdueService = Objects.requireNonNull(overdueService, "overdueService");
    }

    public BookRepository getBookRepository() {
        return bookRepository;
    }

    public LoanRepository getLoanRepository() {
        return loanRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public BookService getBookService() {
        return bookService;
    }

    public LoanService getLoanService() {
        return loanService;
    }

    public OverdueService getOverdueService() {
        return overdueService;
    }
}
