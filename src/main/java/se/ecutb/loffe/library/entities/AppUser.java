package se.ecutb.loffe.library.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static se.ecutb.loffe.library.constants.RegExp.USERNAME_REGEXP_PATTERN;
import static se.ecutb.loffe.library.constants.ValidationMessages.*;

@Data
@Builder
public class AppUser implements Serializable {

    @Id
    private String id;
    @Indexed(unique = true)
    @Pattern(regexp = USERNAME_REGEXP_PATTERN, message = WRONG_LENGTH_MSG + " " + VALID_CHARS_MSG)
    private String username;
    @Size(min = 4, max = 40, message = WRONG_LENGTH_MSG)
    private String password;
    @Email(message = NO_VALID_EMAIL_MSG)
    private String email;
    private List<String> acl;
    private List<Book> loans;


}
