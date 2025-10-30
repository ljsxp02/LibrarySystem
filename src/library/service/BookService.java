package library.service;

import java.util.Objects;

import library.domain.book.Book;
import library.domain.user.User;
import library.exception.AuthException;
import library.exception.BusinessRuleException;
import library.exception.NotFoundException;
import library.exception.ValidationException;
import library.repo.BookRepository;

/**
 * Service for administrator book management operations.
 */
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = Objects.requireNonNull(bookRepository, "bookRepository");
    }

    /**
     * Registers a new book in the catalog.
     *
     * @param requester administrator performing the action
     * @param newBook   book to register
     */
    public void registerBook(User requester, Book newBook) {
        requireAdmin(requester);
        bookRepository.findByIsbn(newBook.getIsbn()).ifPresent(b -> {
            throw new BusinessRuleException("이미 존재하는 ISBN 입니다");
        });
        bookRepository.save(newBook);
    }

    /**
     * Adds stock for an existing book.
     */
    public void addStock(User requester, String isbn, int n) {
        requireAdmin(requester);
        if (n <= 0) {
            throw new ValidationException("수량은 0보다 커야 합니다");
        }
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new NotFoundException("도서를 찾을 수 없습니다"));
        book.addStock(n);
        bookRepository.save(book);
    }

    /**
     * Writes off damaged or lost copies.
     */
    public void writeOff(User requester, String isbn, int n) {
        requireAdmin(requester);
        if (n <= 0) {
            throw new ValidationException("수량은 0보다 커야 합니다");
        }
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new NotFoundException("도서를 찾을 수 없습니다"));
        book.writeOff(n);
        bookRepository.save(book);
    }

    private void requireAdmin(User requester) {
        if (requester == null || !requester.isAdmin()) {
            throw new AuthException("관리자 권한 필요");
        }
    }
}
