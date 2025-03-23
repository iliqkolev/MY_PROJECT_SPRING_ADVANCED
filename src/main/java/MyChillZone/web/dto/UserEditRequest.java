package MyChillZone.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
public class UserEditRequest {

    @Size(max = 20, message = "First name can't have more than 20 symbols.")
    private String firstName;

    @Size(max = 20, message = "Last name can't have more than 20 symbols.")
    private String lastName;

    @Email(message = "Requires correct email format")
    private String email;

    @Size(max = 20, min = 4, message = "Country has to have more than 3 symbols.")
    private String country;

    @URL(message = "Requires correct web link format")
    private String profilePicture;
}
