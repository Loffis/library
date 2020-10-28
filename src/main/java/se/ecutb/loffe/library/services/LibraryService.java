package se.ecutb.loffe.library.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // TODO: split method into several minor methods
    @CachePut(value = "libraryCache", key = "#bookId")
    public void borrowBook(String bookId, String userId) {
        log.info("Trying to borrow book.");
        log.info("User exists: " + appUserRepo.existsById(userId));
        log.info("Book exists: " + bookRepo.existsById(bookId));
        if (appUserRepo.existsById(userId) && bookRepo.existsById(bookId)) {
            log.info("Book id exists. User id exists.");
            var isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().toUpperCase().equals("ROLE_ADMIN"));
            var isLoggedInUser = SecurityContextHolder.getContext().getAuthentication()
                    .getName().toLowerCase().equals(appUserRepo.findById(userId).get().getUsername().toLowerCase());
            if(!isAdmin && !isLoggedInUser) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "You have not authorization to borrow a book for this user.");
            }

            if (bookIsAvailable(bookId)) {
                log.info("Book is available!");

                var book = bookRepo.findById(bookId).get();
                var user = appUserRepo.findById(userId).get();
                List<Book> tempBooks = new ArrayList<>();

                if (user.getLoans() == null || user.getLoans().size() == 0) {
                    log.info("User has no loans.");
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

        } else {
            log.warn("Either book id or user id is missing.");
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Missing id for book or user");
        }
    }

    @CachePut(value = "libraryCache", key = "#bookId")
    public void returnBook(String bookId, String userId) {
        log.info("Try to return book with id " + bookId);
        var book = bookService.findById(bookId);
        var user = appUserRepo.findById(userId).get();

        if (findBook(bookId) && bookIsNotAvailable(bookId) && appUserRepo.existsById(userId)) {
            log.info("Book id exists, book is borrowed and the user exists!");
            List<Book> tempBooks = user.getLoans();
            tempBooks.remove(bookRepo.findById(bookId).get());
            user.setLoans(tempBooks);

            book.setBorrowerId(null);
            book.setAvailable(true);
            bookService.update(bookId, book);
            appUserService.update(userId, user);
            log.info("Book is returned!");
        }
    }

    @CachePut(value = "libraryCache", key = "#userId")
    public void returnAllBooks(String userId) {
        log.info("Try to return all books for user with id " + userId);

        if (!appUserRepo.existsById(userId)) {
            log.warn("Could not find user with id " + userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("User with id '%s' does not exist.", userId));
        }

        var user = appUserRepo.findById(userId).get();
        List<Book> tempBooks = bookRepo.findAll();
        List<Book> usersBooks = user.getLoans();


        for (Book book : tempBooks) {
            if (book.getBorrowerId() != null && book.getBorrowerId().equalsIgnoreCase(userId)) {
                book.setBorrowerId(null);
                usersBooks.remove(book);
            }
        }
        user.setLoans(usersBooks);
        if (user.getLoans().size() > 0) {
            log.warn("Could not return all books. There are still " + user.getLoans().size() + " book(s) left.");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not return all books!");
        }
        appUserRepo.save(user);
        bookRepo.deleteAll();
        bookRepo.insert(tempBooks);
        log.info("All books returned!");
    }

    @Cacheable(value = "libraryCache", key = "#bookId")
    public boolean findBook(String bookId) {
        log.info("Try to find book by id " + bookId);
        if (bookRepo.findById(bookId).isEmpty()) {
            log.warn("Could not find book by id " + bookId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Could not find book with id '%s", bookId));
        }
        log.info("Found book");
        return true;
    }

    @Cacheable(value = "libraryCache", key = "#bookId")
    public boolean bookIsAvailable(String bookId) {
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

    @Cacheable(value = "libraryCache", key = "#bookId")
    public boolean bookIsNotAvailable(String bookId) {
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
