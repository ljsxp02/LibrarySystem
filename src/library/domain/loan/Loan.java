package library.domain.loan;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a loan record.
 */
public class Loan {
    private final String loanId;
    private final String userId;
    private final String isbn;
    private final LocalDate loanDate;
    private final LocalDate dueDate;
    private LocalDate returnedAt;

    /**
     * Creates a loan record.
     *
     * @param userId   user identifier
     * @param isbn     book ISBN
     * @param loanDate loan date
     * @param dueDate  due date
     */
    public Loan(String userId, String isbn, LocalDate loanDate, LocalDate dueDate) {
        this(UUID.randomUUID().toString(), userId, isbn, loanDate, dueDate, null);
    }

    public Loan(String loanId, String userId, String isbn, LocalDate loanDate, LocalDate dueDate, LocalDate returnedAt) {
        this.loanId = Objects.requireNonNull(loanId, "loanId");
        this.userId = Objects.requireNonNull(userId, "userId");
        this.isbn = Objects.requireNonNull(isbn, "isbn");
        this.loanDate = Objects.requireNonNull(loanDate, "loanDate");
        this.dueDate = Objects.requireNonNull(dueDate, "dueDate");
        this.returnedAt = returnedAt;
    }

    public String getLoanId() {
        return loanId;
    }

    public String getUserId() {
        return userId;
    }

    public String getIsbn() {
        return isbn;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnedAt() {
        return returnedAt;
    }

    public void setReturnedAt(LocalDate returnedAt) {
        this.returnedAt = returnedAt;
    }

    /**
     * @return {@code true} if the loan has been returned.
     */
    public boolean isReturned() {
        return returnedAt != null;
    }

    /**
     * Determines if the loan is overdue relative to the provided date.
     *
     * @param today date to compare against
     * @return {@code true} if overdue
     */
    public boolean isOverdue(LocalDate today) {
        return !isReturned() && today.isAfter(dueDate);
    }
}
