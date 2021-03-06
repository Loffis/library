package se.ecutb.loffe.library.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import se.ecutb.loffe.library.entities.Book;
import se.ecutb.loffe.library.services.BookService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<List<Book>> findAllBooks(
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) boolean sortByIsbn,
            @RequestParam(required = false) boolean sortByTitle,
            @RequestParam(required = false) boolean sortByAuthor,
            @RequestParam(required = false) boolean sortByGenre) {
        return ResponseEntity.ok(bookService.findAll(
                isbn, title, author, genre,
                sortByIsbn, sortByTitle, sortByAuthor, sortByGenre));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> findBookById(@PathVariable String id) {
        return ResponseEntity.ok(bookService.findById(id));

    }

    @Secured({"ROLE_LIBRARIAN", "ROLE_ADMIN"})
    @PostMapping
    public ResponseEntity<Book> save(@Validated @RequestBody Book book) {
        return ResponseEntity.ok(bookService.save(book));
    }

    @Secured({"ROLE_LIBRARIAN", "ROLE_ADMIN"})
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable String id, @Validated @RequestBody Book book) {
        bookService.update(id, book);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Secured({"ROLE_LIBRARIAN", "ROLE_ADMIN"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        bookService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
