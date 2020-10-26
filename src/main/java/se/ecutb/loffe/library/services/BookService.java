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

    private final BookRepository bookRepo;

    public List<Book> findAll() {
        return bookRepo.findAll();
    }

    public Book findById(String id) {
        return bookRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Could not find book with id %s.", id)));
    }

    public Book findByIsbn(String isbn) {
        return bookRepo.findByIsbn(isbn).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Could not find book with ISBN '%s'.", isbn)));
    }

    public Book findByAuthor(String author) {
        return null;
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
        bookRepo.deleteById(id);
    }
}
