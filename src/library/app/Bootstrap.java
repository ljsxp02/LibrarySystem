package library.app;

import library.domain.book.Book;
import library.domain.loan.DefaultLoanPolicy;
import library.domain.user.Role;
import library.domain.user.User;
import library.repo.BookRepository;
import library.repo.InMemoryBookRepository;
import library.repo.InMemoryLoanRepository;
import library.repo.InMemoryUserRepository;
import library.repo.LoanRepository;
import library.repo.UserRepository;
import library.service.AuthService;
import library.service.BookService;
import library.service.LoanService;
import library.service.OverdueService;

/**
 * Builds the application context with in-memory repositories and seed data.
 */
public final class Bootstrap {
    private Bootstrap() {
    }

    public static ApplicationContext create() {
        BookRepository bookRepository = new InMemoryBookRepository();
        LoanRepository loanRepository = new InMemoryLoanRepository();
        UserRepository userRepository = new InMemoryUserRepository();

        seed(bookRepository, userRepository);

        AuthService authService = new AuthService(userRepository);
        BookService bookService = new BookService(bookRepository);
        LoanService loanService = new LoanService(bookRepository, loanRepository, new DefaultLoanPolicy());
        OverdueService overdueService = new OverdueService(loanRepository, userRepository, bookRepository);

        return new ApplicationContext(
                bookRepository,
                loanRepository,
                userRepository,
                authService,
                bookService,
                loanService,
                overdueService);
    }

    private static void seed(BookRepository bookRepository, UserRepository userRepository) {
        Book javaBook = new Book("978-1", "자바의 정석", "남궁성", "Programming", 3, 3);
        Book cleanCode = new Book("978-2", "클린 코드", "로버트 마틴", "Programming", 2, 2);
        bookRepository.save(javaBook);
        bookRepository.save(cleanCode);

        User admin = new User("admin", "{noop}admin", "관리자", Role.ADMIN);
        User member = new User("js", "{noop}1234", "이지섭", Role.MEMBER);
        userRepository.save(admin);
        userRepository.save(member);
    }
}
