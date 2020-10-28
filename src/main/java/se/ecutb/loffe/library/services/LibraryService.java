package se.ecutb.loffe.library.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
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

        if (findBook(bookId) && bookIsAvailable(bookId) && userExists(userId)) {

            var book = bookRepo.findById(bookId).get();
            var user = appUserRepo.findById(userId).get();
            List<Book> tempBooks = new ArrayList<>();

            if (user.getLoans() == null) {
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
        }
    }

    public void returnBook(String bookId, String userId) {
        var book = bookService.findById(bookId);
        var user = appUserRepo.findById(userId).get();

        if (findBook(bookId) && bookIsNotAvailable(bookId) && userExists(userId)) {
            List<Book> tempBooks = user.getLoans();
            tempBooks.remove(bookId);
            user.setLoans(tempBooks);

            book.setBorrowerId(null);
            book.setAvailable(true);
            bookService.update(bookId, book);
            appUserService.update(userId, user);
        }
    }

    public void returnAllBooks(String userId) {

        if (!userExists(userId)) {
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
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Could not return all books!");
        }
        user.setLoans(null);
        bookRepo.deleteAll();
        bookRepo.insert(tempBooks);
    }

    private boolean findBook(String bookId) {
        if (bookRepo.findById(bookId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Could not find book with id '%s", bookId));
        }
        return true;
    }

    private boolean bookIsAvailable(String bookId) {
        var book = bookService.findById(bookId);

        if (!book.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("Book '%s' is currently unavailable.", book.getTitle()));
        }
        return true;
    }

    private boolean bookIsNotAvailable(String bookId) {
        var book = bookService.findById(bookId);
        if (!book.isAvailable()) {
            return true;
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT,
                String.format("Book '%s' is already in the library!", book.getTitle()));
    }

    private boolean userExists(String userId) {
        if (!appUserRepo.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Could not find the user by id '%s'.", userId));
        }
        return true;
    }
}
