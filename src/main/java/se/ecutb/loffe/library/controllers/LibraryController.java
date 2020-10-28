package se.ecutb.loffe.library.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.ecutb.loffe.library.services.LibraryService;

@RestController
@RequestMapping("/api/v1/loans")
public class LibraryController {

    @Autowired
    private LibraryService libraryService;

    @PutMapping("/{id}")
    public ResponseEntity<Void> loan(@PathVariable String id, @RequestParam String userId) {
        libraryService.borrowBook(id, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/return/{id}")
    public ResponseEntity<Void> returnLoan(@PathVariable String bookId, @RequestParam String userId) {
        libraryService.returnBook(bookId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/returnall/{id}")
    public ResponseEntity<Void> returnAll(@PathVariable String id) {
        libraryService.returnAllBooks(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }



}
