package library.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import library.app.ApplicationContext;
import library.domain.book.Book;
import library.domain.user.User;
import library.exception.AuthException;
import library.exception.BusinessRuleException;
import library.exception.NotFoundException;
import library.exception.ValidationException;
import library.service.AuthService;
import library.service.BookService;
import library.service.LoanService;
import library.service.OverdueService;
import library.service.OverdueService.OverdueEntry;

/**
 * Parses console commands and delegates to application services.
 */
public class CommandProcessor {
    private final ConsoleIO console;
    private final AuthService authService;
    private final BookService bookService;
    private final LoanService loanService;
    private final OverdueService overdueService;
    private final ApplicationContext context;

    private User session;

    public CommandProcessor(ConsoleIO console, ApplicationContext context) {
        this.console = console;
        this.context = context;
        this.authService = context.getAuthService();
        this.bookService = context.getBookService();
        this.loanService = context.getLoanService();
        this.overdueService = context.getOverdueService();
    }

    /**
     * Handles a single command line input.
     *
     * @param line raw input line
     * @return {@code false} if the processor requested termination
     */
    public boolean handle(String line) {
        if (line == null) {
            return true;
        }
        String trimmed = line.trim();
        if (trimmed.isEmpty()) {
            return true;
        }

        String[] parts = trimmed.split("\\s+");
        String commandToken = parts[0];
        String command = commandToken.toLowerCase(Locale.ROOT);

        try {
            return switch (command) {
                case "help" -> {
                    HelpPrinter.print(console);
                    yield true;
                }
                case "exit" -> {
                    console.println("프로그램을 종료합니다.");
                    yield false;
                }
                case "register" -> {
                    handleRegister();
                    yield true;
                }
                case "login" -> {
                    handleLogin();
                    yield true;
                }
                case "logout" -> {
                    session = null;
                    console.println("로그아웃되었습니다.");
                    yield true;
                }
                case "search" -> {
                    handleSearch(parts, "사용법: search <keyword>");
                    yield true;
                }
                case "loan" -> {
                    handleLoan(trimmed, commandToken);
                    yield true;
                }
                case "return" -> {
                    handleReturn(trimmed, commandToken);
                    yield true;
                }
                case "addstock" -> {
                    handleAddStock(parts);
                    yield true;
                }
                case "writeoff" -> {
                    handleWriteOff(parts);
                    yield true;
                }
                case "overdue" -> {
                    handleOverdue();
                    yield true;
                }
                default -> {
                    console.println("알 수 없는 명령입니다. help를 입력하세요.");
                    yield true;
                }
            };
        } catch (AuthException | NotFoundException | BusinessRuleException | ValidationException e) {
            console.println("[오류] " + e.getMessage());
        } catch (Exception e) {
            console.println("[예상치 못한 오류] " + e.getMessage());
        }
        return true;
    }

    private void handleRegister() {
        String id = readRequiredLine("id: ").trim();
        String pw = readRequiredLine("pw: ").trim();
        String name = readRequiredLine("이름: ").trim();
        User newUser = authService.register(id, pw, name);
        console.println("회원가입 완료: " + newUser.getName());
    }

    private void handleLogin() {
        String id = readRequiredLine("id: ").trim();
        String pw = readRequiredLine("pw: ").trim();
        session = authService.login(id, pw);
        console.println("로그인: " + session.getName() + " (" + session.getRole() + ")");
    }

    private void handleSearch(String[] parts, String usage) {
        String keyword = requireArg(parts, 1, usage);
        List<Book> books = context.getBookRepository().searchByTitle(keyword);
        for (Book book : books) {
            console.println(book.getTitle() + " | " + book.getAuthor() + " | 재고:" + book.getAvailable());
        }
    }

    private void handleLoan(String line, String commandToken) {
        requireLogin();
        String token = requireRemaining(line, commandToken, "사용법: loan <isbn|title>");
        LocalDate today = LocalDate.now();
        if (looksLikeIsbn(token)) {
            loanService.loan(session, token, today);
        } else {
            loanService.loanByTitle(session, token, today);
        }
        console.println("대출 완료: " + token);
    }

    private void handleReturn(String line, String commandToken) {
        requireLogin();
        String token = requireRemaining(line, commandToken, "사용법: return <isbn|title>");
        LocalDate today = LocalDate.now();
        if (looksLikeIsbn(token)) {
            loanService.returnBook(session, token, today);
        } else {
            loanService.returnByTitle(session, token, today);
        }
        console.println("반납 완료: " + token);
    }

    private void handleAddStock(String[] parts) {
        requireLogin();
        String isbn = requireArg(parts, 1, "사용법: addstock <isbn> <n>");
        String countToken = requireArg(parts, 2, "사용법: addstock <isbn> <n>");
        int quantity = parseInt(countToken, "수량은 숫자여야 합니다");
        bookService.addStock(session, isbn, quantity);
        console.println("입고 완료");
    }

    private void handleWriteOff(String[] parts) {
        requireLogin();
        String isbn = requireArg(parts, 1, "사용법: writeoff <isbn> <n>");
        String countToken = requireArg(parts, 2, "사용법: writeoff <isbn> <n>");
        int quantity = parseInt(countToken, "수량은 숫자여야 합니다");
        bookService.writeOff(session, isbn, quantity);
        console.println("폐기/손실 처리 완료");
    }

    private void handleOverdue() {
        requireLogin();
        List<OverdueEntry> overdues = overdueService.listOverdues(session, LocalDate.now());
        for (OverdueEntry entry : overdues) {
            console.println(entry.user().getName() + " | " + entry.book().getTitle()
                    + " | DUE:" + entry.dueDate() + " | +" + entry.overdueDays() + "일");
        }
    }

    private String readRequiredLine(String prompt) {
        String line = console.readLine(prompt);
        if (line == null) {
            throw new ValidationException("입력이 취소되었습니다.");
        }
        return line;
    }

    private void requireLogin() {
        if (session == null) {
            throw new AuthException("로그인 필요");
        }
    }

    private String requireArg(String[] parts, int index, String usage) {
        if (parts.length <= index) {
            throw new ValidationException(usage);
        }
        return parts[index];
    }

    private String requireRemaining(String line, String commandToken, String usage) {
        String remaining = line.substring(commandToken.length()).trim();
        if (remaining.isEmpty()) {
            throw new ValidationException(usage);
        }
        return remaining;
    }

    private int parseInt(String value, String message) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ValidationException(message);
        }
    }

    private boolean looksLikeIsbn(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (!(Character.isDigit(c) || c == '-' || c == 'X' || c == 'x')) {
                return false;
            }
        }
        return true;
    }
}
