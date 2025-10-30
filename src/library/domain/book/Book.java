package library.domain.book;

import java.util.Objects;

import library.exception.BusinessRuleException;

/**
 * Represents a book managed by the library.
 */
public class Book {
    private final String isbn;
    private String title;
    private String author;
    private String category;
    private int total;
    private int available;

    /**
     * Creates a book instance.
     *
     * @param isbn      unique ISBN identifier
     * @param title     book title
     * @param author    book author
     * @param category  book category
     * @param total     total copies
     * @param available available copies
     */
    public Book(String isbn, String title, String author, String category, int total, int available) {
        this.isbn = Objects.requireNonNull(isbn, "isbn");
        this.title = Objects.requireNonNull(title, "title");
        this.author = Objects.requireNonNull(author, "author");
        this.category = Objects.requireNonNull(category, "category");
        if (total < 0) {
            throw new BusinessRuleException("총 보유 수량은 0 이상이어야 합니다");
        }
        if (available < 0 || available > total) {
            throw new BusinessRuleException("대출 가능 수량이 유효하지 않습니다");
        }
        this.total = total;
        this.available = available;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public int getTotal() {
        return total;
    }

    public int getAvailable() {
        return available;
    }

    /**
     * Adds new stock for the book. Administrator-only.
     *
     * @param n number of copies to add
     */
    public void addStock(int n) {
        if (n <= 0) {
            throw new BusinessRuleException("추가 수량은 0보다 커야 합니다");
        }
        total += n;
        available += n;
    }

    /**
     * Removes damaged or lost copies from the inventory. Administrator-only.
     *
     * @param n number of copies to write off
     */
    public void writeOff(int n) {
        if (n <= 0) {
            throw new BusinessRuleException("폐기 수량은 0보다 커야 합니다");
        }
        if (n > available) {
            throw new BusinessRuleException("폐기 수량이 대출 가능 수량을 초과합니다");
        }
        total -= n;
        available -= n;
    }

    /**
     * Marks a copy as loaned out.
     */
    public void takeOne() {
        if (available <= 0) {
            throw new BusinessRuleException("재고 부족");
        }
        available--;
    }

    /**
     * Marks a copy as returned.
     */
    public void returnOne() {
        if (available + 1 > total) {
            throw new BusinessRuleException("재고 수량 불일치");
        }
        available++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Book book = (Book) o;
        return isbn.equals(book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }
}
