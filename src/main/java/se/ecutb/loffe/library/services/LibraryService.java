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

        if (bookRepo.findById(bookId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Could not find book with id '%s", bookId));
        }

        var book = bookService.findById(bookId);

        if (!book.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("Book '%s' is currently unavailable.", book.getTitle()));
        }

        if (!appUserRepo.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Could not find the user by id '%s'.", userId));
        }
        var user = appUserRepo.findById(userId).get();

        if (user.getLoans() == null) {
            List<Book> tempBooks = new ArrayList<>();
            tempBooks.add(book);
            user.setLoans(tempBooks);

            book.setAvailable(false);
            book.setBorrowerId(userId);
            book.setId(bookId);
            bookService.update(bookId, book);
            appUserService.update(userId, user);
        }




    }

}
