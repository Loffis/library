package se.ecutb.loffe.library.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class AppUser implements Serializable {
    @Id
    private String id;
    @Indexed(unique = true)
    @Pattern(regexp = "^[a-z]+[a-z0-9]{2,19}$",
            message = "Username must be 3-20 characters. Valid characters: a-z, 0-9. Start with a-z.")
    private String username;
    @Size(min = 4, max = 40, message = "Password must be 4-40 characters.")
    private String password;
    @Email(message = "Not a valid email address")
    private String email;
    private List<String> acl;

}
