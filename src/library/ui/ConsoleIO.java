package library.ui;

import java.io.Closeable;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Scanner;

/**
 * Lightweight wrapper around {@link Scanner} for console interaction.
 */
public class ConsoleIO implements Closeable {
    private final Scanner scanner;
    private final PrintStream out;

    public ConsoleIO(InputStream in, PrintStream out) {
        this.scanner = new Scanner(Objects.requireNonNull(in, "in"));
        this.out = Objects.requireNonNull(out, "out");
    }

    public String readLine(String prompt) {
        if (prompt != null) {
            out.print(prompt);
        }
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine();
    }

    public void println(String message) {
        out.println(message);
    }

    public void print(String message) {
        out.print(message);
    }

    @Override
    public void close() {
        scanner.close();
    }
}
