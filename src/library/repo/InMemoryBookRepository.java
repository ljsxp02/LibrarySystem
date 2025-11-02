package library.repo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import library.domain.book.Book;

/**
 * In-memory implementation of {@link BookRepository}.
 */
public class InMemoryBookRepository implements BookRepository {
    private final Map<String, Book> booksByIsbn = new ConcurrentHashMap<>();

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return Optional.ofNullable(booksByIsbn.get(isbn));
    }

    @Override
    public List<Book> findByTitleIgnoreCase(String title) {
        String normalized = title == null ? "" : title.toLowerCase(Locale.ROOT);
        List<Book> result = new ArrayList<>();
        for (Book book : booksByIsbn.values()) {
            if (book.getTitle().toLowerCase(Locale.ROOT).equals(normalized)) {
                result.add(book);
            }
        }
        return result;
    }

    @Override
    public List<Book> searchByTitle(String keyword) {
        String lower = keyword == null ? "" : keyword.toLowerCase(Locale.ROOT);
        List<Book> result = new ArrayList<>();
        for (Book book : booksByIsbn.values()) {
            if (book.getTitle().toLowerCase(Locale.ROOT).contains(lower)) {
                result.add(book);
            }
        }
        return result;
    }

    @Override
    public void save(Book book) {
        booksByIsbn.put(book.getIsbn(), book);
    }

    @Override
    public Collection<Book> findAll() {
        return List.copyOf(booksByIsbn.values());
    }
}
