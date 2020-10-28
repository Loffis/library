package se.ecutb.loffe.library.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.ecutb.loffe.library.entities.Book;
import se.ecutb.loffe.library.repositories.AppUserRepository;
import se.ecutb.loffe.library.repositories.BookRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LibraryService {

    public final BookService bookService;
    public final AppUserService appUserService;
    public final AppUserRepository appUserRepo;
    public final BookRepository bookRepo;

    public void borrowBook(String bookId, String userId) {
        log.info("Trying to borrow book with id " + bookId);

        if (findBook(bookId) && bookIsAvailable(bookId) && appUserRepo.existsById(userId)) {
            log.info("Book id exists, book is available and the user exists!");

            var book = bookRepo.findById(bookId).get();
            var user = appUserRepo.findById(userId).get();
            List<Book> tempBooks = new ArrayList<>();

            if (user.getLoans() == null) {
                log.info("User has no loans before.");
                tempBooks.add(book);
            } else {
                tempBooks = user.getLoans();
                tempBooks.add(book);
            }

            book.setAvailable(false);
            book.setBorrowerId(userId);
            user.setLoans(tempBooks);
            bookService.update(bookId, book);
            appUserService.update(userId, user);
            log.info("User " + user.getUsername() + " has borrowed " + book.getTitle() + "!");
        }
    }

    public void returnBook(String bookId, String userId) {
        log.info("Try to return book with id " + bookId);
        var book = bookService.findById(bookId);
        var user = appUserRepo.findById(userId).get();

        if (findBook(bookId) && bookIsNotAvailable(bookId) && appUserRepo.existsById(userId)) {
            List<Book> tempBooks = user.getLoans();
            tempBooks.remove(bookId);
            user.setLoans(tempBooks);

            book.setBorrowerId(null);
            book.setAvailable(true);
            bookService.update(bookId, book);
            appUserService.update(userId, user);
            log.info("Book is returned!");
        }
        log.warn("Book could not be returned.");
    }

    public void returnAllBooks(String userId) {
        log.info("Try to return all books for user with id " + userId);

        if (!appUserRepo.existsById(userId)) {
            log.warn("Could not find user with id " + userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("User with id '%s' does not exist.", userId));
        }

        var user = appUserRepo.findById(userId).get();
        List<Book> tempBooks = bookRepo.findAll();

        for (Book book : tempBooks) {
            if (book.getBorrowerId() != null && book.getBorrowerId().equalsIgnoreCase(userId)) {
                book.setBorrowerId(null);
            }
        }
        if (user.getLoans().size() > 0) {
            log.warn("Could not return all books. There are still " + user.getLoans().size() + " books left.");
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Could not return all books!");
        }
        user.setLoans(null);
        bookRepo.deleteAll();
        bookRepo.insert(tempBooks);
        log.info("All books returned!");
    }

    private boolean findBook(String bookId) {
        log.info("Try to find book by id " + bookId);
        if (bookRepo.findById(bookId).isEmpty()) {
            log.warn("Could not find book by id " + bookId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Could not find book with id '%s", bookId));
        }
        log.info("Found book");
        return true;
    }

    private boolean bookIsAvailable(String bookId) {
        log.info("Checking if book with id " + bookId + " is available.");
        var book = bookService.findById(bookId);

        if (!book.isAvailable()) {
            log.warn("The book is not available.");
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("Book '%s' is currently unavailable.", book.getTitle()));
        }
        log.info("Book is available.");
        return true;
    }

    private boolean bookIsNotAvailable(String bookId) {
        log.info("Checking if book by id " + bookId + " is borrowed.");
        var book = bookService.findById(bookId);
        if (!book.isAvailable()) {
            log.info("Book is borrowed.");
            return true;
        }
        log.warn("Book is not borrowed by anyone.");
        throw new ResponseStatusException(HttpStatus.CONFLICT,
                String.format("Book '%s' is already in the library!", book.getTitle()));
    }
}
