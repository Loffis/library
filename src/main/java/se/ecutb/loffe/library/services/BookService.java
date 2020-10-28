package se.ecutb.loffe.library.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.ecutb.loffe.library.entities.Book;
import se.ecutb.loffe.library.repositories.BookRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookService {

    public final BookRepository bookRepo;

    @Cacheable(value = "libraryCache")
    public List<Book> findAll(String isbn, String title, String author, String genre,
                              boolean sortByIsbn, boolean sortByTitle, boolean sortByAuthor, boolean sortByGenre) {
        log.info("Starting retrieving books...");
        var books = bookRepo.findAll();

        if (isbn != null) {
            log.info("Search by isbn " + isbn);
            books = books.stream()
                    .filter(book -> book.getIsbn().contains(isbn))
                    .collect(Collectors.toList());
        }

        if (title != null) {
            log.info("Search by title " + title);
            books = books.stream()
                    .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (author != null) {
            log.info("Search by author " + author);
            books = books.stream()
                    .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (genre != null) {
            log.info("Search by genre " + genre);
            books = books.stream()
                    .filter(book -> book.getGenres().contains(genre.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (sortByIsbn) {
            log.info("Sorting by isbn.");
            books.sort(Comparator.comparing(Book::getIsbn));
        }

        if (sortByTitle) {
            log.info("Sorting by title.");
            books.sort(Comparator.comparing(Book::getTitle));
        }

        if (sortByAuthor) {
            log.info("Sorting by author.");
            books.sort(Comparator.comparing(Book::getAuthor));
        }

        if (sortByGenre) {
            log.info("Sorting by genre.");
            books.sort(Comparator.comparing(book -> book.getGenres().get(0)));
        }

        log.info(books.size() + " book(s) delivered!");
        return books;
    }

    @Cacheable(value = "libraryCache", key = "#id")
    public Book findById(String id) {
        log.info("Retrieving book by id " + id);
        return bookRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Could not find book with id %s.", id)));
    }

    @CachePut(value = "libraryCache", key = "#result.id")
    public Book save(Book book) {
        log.info("Saving book with title " + book.getTitle());
        return bookRepo.save(book);
    }

    @CachePut(value = "libraryCache", key = "#id")
    public void update(String id, Book book) {
        log.info("Updating book with id " + id);
        if (!bookRepo.existsById(id)) {
            log.warn("Could not find book with id " + id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Could not find book with id '%s", id));
        }
        book.setId(id);
        bookRepo.save(book);
        log.info("Book with id " + id + " is saved.");
    }

    @CacheEvict(value = "libraryCache", key = "#id")
    public void delete(String id) {
        log.info("Deleting book with id " + id);
        if (!bookRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Could not find book with id '%s", id));
        }
        // var book = bookRepo.findById(id).get();
        // injectar jag LibraryService får jag "circular dependency injection"
        // returnBook(id, book.getBorrowerId());
        bookRepo.deleteById(id);
        log.info("Book with id " + id + " is deleted!");
    }
}
