package library.repo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import library.domain.loan.Loan;

/**
 * In-memory implementation of {@link LoanRepository}.
 */
public class InMemoryLoanRepository implements LoanRepository {
    private final Map<String, Loan> loansById = new ConcurrentHashMap<>();

    @Override
    public void save(Loan loan) {
        loansById.put(loan.getLoanId(), loan);
    }

    @Override
    public Optional<Loan> findActiveByUserAndIsbn(String userId, String isbn) {
        return loansById.values().stream()
                .filter(loan -> loan.getUserId().equals(userId))
                .filter(loan -> loan.getIsbn().equals(isbn))
                .filter(loan -> loan.getReturnedAt() == null)
                .findFirst();
    }

    @Override
    public List<Loan> findActiveByUser(String userId) {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : loansById.values()) {
            if (loan.getUserId().equals(userId) && loan.getReturnedAt() == null) {
                result.add(loan);
            }
        }
        return result;
    }

    @Override
    public List<Loan> findActive() {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : loansById.values()) {
            if (loan.getReturnedAt() == null) {
                result.add(loan);
            }
        }
        return result;
    }

    @Override
    public List<Loan> findAll() {
        return new ArrayList<>(loansById.values());
    }
}
