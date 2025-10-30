package library.repo;

import java.util.List;
import java.util.Optional;

import library.domain.loan.Loan;

/**
 * Repository for accessing loan records.
 */
public interface LoanRepository {
    void save(Loan loan);

    Optional<Loan> findActiveByUserAndIsbn(String userId, String isbn);

    List<Loan> findActiveByUser(String userId);

    List<Loan> findActive();

    List<Loan> findAll();
}
