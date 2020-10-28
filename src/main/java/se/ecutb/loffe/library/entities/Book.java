package se.ecutb.loffe.library.entities;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.ISBN;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

import static se.ecutb.loffe.library.constants.Messages.*;

@Data
@Builder
public class Book implements Serializable {

    private static final long serialVersionUID = -3308181849993556120L;

    @Id
    private String id;
    @ISBN(type = ISBN.Type.ANY, message = NO_VALID_ISBN_MSG)
    private String isbn;
    @NotEmpty(message = FIELD_REQUIRED_MSG)
    private String title;
    @NotEmpty(message = FIELD_REQUIRED_MSG)
    private String author;
    @NotEmpty(message = FIELD_REQUIRED_MSG)
    private List<String> genres;
    @Size(min = 5, max = 1000, message = WRONG_LENGTH_MSG)
    private String plot;
    private boolean isAvailable;
    private String borrowerId;

}
