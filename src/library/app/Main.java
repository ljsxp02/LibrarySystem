package library.app;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import library.domain.book.Book;
import library.domain.loan.DefaultLoanPolicy;
import library.domain.user.Role;
import library.domain.user.User;
import library.exception.AuthException;
import library.exception.BusinessRuleException;
import library.exception.NotFoundException;
import library.exception.ValidationException;
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
import library.service.OverdueService.OverdueEntry;

/**
 * Console application entry point.
 */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        BookRepository bookRepository = new InMemoryBookRepository();
        LoanRepository loanRepository = new InMemoryLoanRepository();
        UserRepository userRepository = new InMemoryUserRepository();

        seedData(bookRepository, userRepository);

        AuthService authService = new AuthService(userRepository);
        BookService bookService = new BookService(bookRepository);
        LoanService loanService = new LoanService(bookRepository, loanRepository, new DefaultLoanPolicy());
        OverdueService overdueService = new OverdueService(loanRepository, userRepository, bookRepository);

        Scanner scanner = new Scanner(System.in);
        User session = null;
        System.out.println("help 명령으로 도움말을 확인하세요.");
        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\s+");
            String command = parts[0].toLowerCase(Locale.ROOT);
            try {
                switch (command) {
                    case "help" -> printHelp();
                    case "exit" -> {
                        System.out.println("프로그램을 종료합니다.");
                        scanner.close();
                        return;
                    }
                    case "login" -> {
                        System.out.print("id: ");
                        String id = scanner.nextLine().trim();
                        System.out.print("pw: ");
                        String pw = scanner.nextLine().trim();
                        session = authService.login(id, pw);
                        System.out.println("로그인: " + session.getName() + " (" + session.getRole() + ")");
                    }
                    case "logout" -> {
                        session = null;
                        System.out.println("로그아웃되었습니다.");
                    }
                    case "search" -> {
                        String keyword = requireArg(parts, 1, "사용법: search <keyword>");
                        List<Book> books = bookRepository.searchByTitle(keyword);
                        for (Book book : books) {
                            System.out.println(book.getTitle() + " | " + book.getAuthor() + " | 재고:" + book.getAvailable());
                        }
                    }
                    case "loan" -> {
                        String isbn = requireArg(parts, 1, "사용법: loan <isbn>");
                        requireLogin(session);
                        loanService.loan(session, isbn, LocalDate.now());
                        System.out.println("대출 완료");
                    }
                    case "return" -> {
                        String isbn = requireArg(parts, 1, "사용법: return <isbn>");
                        requireLogin(session);
                        loanService.returnBook(session, isbn, LocalDate.now());
                        System.out.println("반납 완료");
                    }
                    case "addstock" -> {
                        String isbn = requireArg(parts, 1, "사용법: addstock <isbn> <n>");
                        String nStr = requireArg(parts, 2, "사용법: addstock <isbn> <n>");
                        int n = parseInt(nStr, "수량은 숫자여야 합니다");
                        requireLogin(session);
                        bookService.addStock(session, isbn, n);
                        System.out.println("입고 완료");
                    }
                    case "writeoff" -> {
                        String isbn = requireArg(parts, 1, "사용법: writeoff <isbn> <n>");
                        String nStr = requireArg(parts, 2, "사용법: writeoff <isbn> <n>");
                        int n = parseInt(nStr, "수량은 숫자여야 합니다");
                        requireLogin(session);
                        bookService.writeOff(session, isbn, n);
                        System.out.println("폐기/손실 처리 완료");
                    }
                    case "overdue" -> {
                        requireLogin(session);
                        List<OverdueEntry> overdues = overdueService.listOverdues(session, LocalDate.now());
                        for (OverdueEntry entry : overdues) {
                            System.out.println(entry.user().getName() + " | " + entry.book().getTitle() +
                                    " | DUE:" + entry.dueDate() + " | +" + entry.overdueDays() + "일");
                        }
                    }
                    default -> System.out.println("알 수 없는 명령입니다. help를 입력하세요.");
                }
            } catch (AuthException | NotFoundException | BusinessRuleException | ValidationException e) {
                System.out.println("[오류] " + e.getMessage());
            } catch (Exception e) {
                System.out.println("[예상치 못한 오류] " + e.getMessage());
            }
        }
    }

    private static void seedData(BookRepository bookRepository, UserRepository userRepository) {
        Book javaBook = new Book("978-1", "자바의 정석", "남궁성", "Programming", 3, 3);
        Book cleanCode = new Book("978-2", "클린 코드", "로버트 마틴", "Programming", 2, 2);
        bookRepository.save(javaBook);
        bookRepository.save(cleanCode);

        User admin = new User("admin", "{noop}admin", "관리자", Role.ADMIN);
        User member = new User("js", "{noop}1234", "이지섭", Role.MEMBER);
        userRepository.save(admin);
        userRepository.save(member);
    }

    private static String requireArg(String[] parts, int index, String usage) {
        if (parts.length <= index) {
            throw new ValidationException(usage);
        }
        return parts[index];
    }

    private static void requireLogin(User session) {
        if (session == null) {
            throw new AuthException("로그인 필요");
        }
    }

    private static int parseInt(String value, String message) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ValidationException(message);
        }
    }

    private static void printHelp() {
        System.out.println("명령어:");
        System.out.println("  login / logout");
        System.out.println("  search <keyword>");
        System.out.println("  loan <isbn>");
        System.out.println("  return <isbn>");
        System.out.println("  (관리자) addstock <isbn> <n>");
        System.out.println("  (관리자) writeoff <isbn> <n>");
        System.out.println("  (관리자) overdue");
        System.out.println("  exit");
    }
}
