package se.ecutb.loffe.library.entities;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Book implements Serializable {
    String id;
    String isbn;
    String title;
    String author;
    String genre;
    String plot;
    boolean isAvailable;
}
