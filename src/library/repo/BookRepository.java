package library.repo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import library.domain.book.Book;

/**
 * Repository for accessing books.
 */
public interface BookRepository {
    Optional<Book> findByIsbn(String isbn);

    /**
     * Finds books whose title exactly matches (ignoring case) the given title.
     */
    List<Book> findByTitleIgnoreCase(String title);

    /**
     * Searches for books whose title contains the given keyword (case-insensitive).
     */
    List<Book> searchByTitle(String keyword);

    void save(Book book);

    Collection<Book> findAll();
}
