package se.ecutb.loffe.library.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import se.ecutb.loffe.library.entities.Book;
import se.ecutb.loffe.library.services.AppUserService;
import se.ecutb.loffe.library.services.BookService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    @Autowired
    private BookService bookService;


    @GetMapping
    public ResponseEntity<List<Book>> findAllBooks() {
        return ResponseEntity.ok(bookService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> findBookById(@PathVariable String id) {
        return ResponseEntity.ok(bookService.findById(id));

    }

    @PostMapping
    public ResponseEntity<Book> save(@Validated @RequestBody Book book) {
        return ResponseEntity.ok(bookService.save(book));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable String id, @Validated @RequestBody Book book) {
        bookService.update(id, book);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        bookService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
