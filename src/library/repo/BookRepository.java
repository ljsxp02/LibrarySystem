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

    List<Book> searchByTitle(String keyword);

    void save(Book book);

    Collection<Book> findAll();
}
