package se.ecutb.loffe.library.entities;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.ISBN;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Data
@Builder
public class Book implements Serializable {
    @Id
    private String id;
    @ISBN(type = ISBN.Type.ANY, message = "Not a valid ISBN number")
    private String isbn;
    private String title;
    private String author;
    private String genre;
    private String plot;
    boolean isAvailable;
}
