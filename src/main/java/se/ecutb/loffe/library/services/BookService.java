package se.ecutb.loffe.library.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.ecutb.loffe.library.entities.Book;
import se.ecutb.loffe.library.repositories.BookRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookService {

    public final BookRepository bookRepo;


    public List<Book> findAll(String isbn, String title, String author, String genre,
                              boolean sortByIsbn, boolean sortByTitle, boolean sortByAuthor, boolean sortByGenre) {
        var books = bookRepo.findAll();

        return books;
    }

    public Book findById(String id) {
        return bookRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Could not find book with id %s.", id)));
    }

    public Book save(Book book) {
        return bookRepo.save(book);
    }

    public void update(String id, Book book) {
        if (!bookRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Could not find book with id '%s", id));
        }
        book.setId(id);
        bookRepo.save(book);
    }



    public void delete(String id) {
        if (!bookRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Could not find book with id '%s", id));
        }
        // var book = bookRepo.findById(id).get();
        // injectar jag LibraryService får jag "circular dependency injection"
        // returnBook(id, book.getBorrowerId());
        bookRepo.deleteById(id);
    }

}
