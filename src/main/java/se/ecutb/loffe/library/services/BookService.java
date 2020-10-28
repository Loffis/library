package se.ecutb.loffe.library.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


    public List<Book> findAll(String isbn, String title, String author, String genre,
                              boolean sortByIsbn, boolean sortByTitle, boolean sortByAuthor, boolean sortByGenre) {
        var books = bookRepo.findAll();

        if (isbn != null) {
            books = books.stream()
                    .filter(book -> book.getIsbn().contains(isbn))
                    .collect(Collectors.toList());
        }

        if (title != null) {
            books = books.stream()
                    .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (author != null) {
            books = books.stream()
                    .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (genre != null) {
            books = books.stream()
                    .filter(book -> book.getGenres().contains(genre.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (sortByIsbn) {
            books.sort(Comparator.comparing(Book::getIsbn));
        }

        if (sortByTitle) {
            books.sort(Comparator.comparing(Book::getTitle));
        }

        if (sortByAuthor) {
            books.sort(Comparator.comparing(Book::getAuthor));
        }

        if (sortByGenre) {
            books.sort(Comparator.comparing(book -> book.getGenres().get(0)));
        }

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
