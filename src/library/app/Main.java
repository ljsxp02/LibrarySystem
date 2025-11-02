package library.app;

import library.ui.CommandProcessor;
import library.ui.ConsoleIO;

/**
 * Console application entry point.
 */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        ApplicationContext context = Bootstrap.create();
        ConsoleIO console = new ConsoleIO(System.in, System.out);
        CommandProcessor processor = new CommandProcessor(console, context);

        console.println("help 명령으로 도움말을 확인하세요.");
        while (true) {
            String line = console.readLine("> ");
            if (line == null) {
                break;
            }
            if (!processor.handle(line)) {
                break;
            }
        }
        console.close();
    }
}
