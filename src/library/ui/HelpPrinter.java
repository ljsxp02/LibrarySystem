package library.ui;

/**
 * Prints available commands to the console.
 */
public final class HelpPrinter {
    private HelpPrinter() {
    }

    public static void print(ConsoleIO console) {
        console.println("명령어:");
        console.println("  register");
        console.println("  login / logout");
        console.println("  search <keyword>");
        console.println("  loan <isbn|title>");
        console.println("  return <isbn|title>");
        console.println("  (관리자) addstock <isbn> <n>");
        console.println("  (관리자) writeoff <isbn> <n>");
        console.println("  (관리자) overdue");
        console.println("  exit");
    }
}
