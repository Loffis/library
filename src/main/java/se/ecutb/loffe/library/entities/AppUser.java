package se.ecutb.loffe.library.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

import static se.ecutb.loffe.library.constants.RegExp.USERNAME_REGEXP_PATTERN;
import static se.ecutb.loffe.library.constants.Messages.*;

@Data
@Builder
public class AppUser implements Serializable {

    private static final long serialVersionUID = -2118771206157720830L;

    @Id
    private String id;
    @Indexed(unique = true) // Doesn't work without a MongoTemplate. Not implemented (yet).
    @Pattern(regexp = USERNAME_REGEXP_PATTERN, message = WRONG_USERNAME_MSG)
    private String username;
    @JsonIgnore
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 4, max = 40, message = WRONG_LENGTH_MSG)
    private String password;
    @Email(message = NO_VALID_EMAIL_MSG)
    private String email;
    private List<String> acl;
    private List<Book> loans;
}
