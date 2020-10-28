package se.ecutb.loffe.library.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import se.ecutb.loffe.library.repositories.BookRepository;
import se.ecutb.loffe.library.services.BookService;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {
    private static String currentDir = System.getProperty("user.dir");
    private static String uploadDir = currentDir + "/src/main/resources/static/uploads";
    final List<String> supportedFileExtensions = List.of(".png,.jpg,.jpeg,.gif".split(","));

    @Autowired
    private BookRepository bookRepo;
    private BookService bookService;

    @PostConstruct
    public void init() {
        File uploadsPath = new File(uploadDir);
        if (!uploadsPath.exists())  {
            uploadsPath.mkdir();
        }
    }

    @PostMapping
    public ResponseEntity<String> uploadImage(@RequestParam MultipartFile upload, @RequestParam String id) {

        if (!bookRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Book id not found");
        }
        var book = bookRepo.findById(id).get();
        var fileName = upload.getOriginalFilename();
        var extension = fileName.substring(fileName.lastIndexOf("."));

        if (!supportedFileExtensions.contains(extension)) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
        }

        var target = new File(uploadDir + File.separator + fileName);
        try {
            upload.transferTo(target);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getLocalizedMessage());
        }
        book.setImage(target);
        bookRepo.save(book);
        return ResponseEntity.created(URI.create("/files/" + fileName)).build();
    }
}
