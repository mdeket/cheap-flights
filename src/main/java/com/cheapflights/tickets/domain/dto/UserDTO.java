package com.cheapflights.tickets.domain.dto;

import com.cheapflights.tickets.config.security.AuthorityConstants;
import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class UserDTO {

    private Long id;

    @NotNull(message = "username must not be null.")
    @Size(min = 4, max = 50, message = "username length must be between 4 and 50 characters.")
    private String username;

    @NotNull(message = "firstName must not be null.")
    @Size(min = 1, max = 30, message = "firstName length must be between 1 and 30 characters.")
    private String firstName;

    @NotNull(message = "lastName must not be null.")
    @Size(min = 1, max = 30, message = "lastName length must be between 1 and 30 characters.")
    private String lastName;

    @NotNull(message = "password must not be null.")
    @Size(min = 4, max = 128, message = "password length must be longer than 4 character.")
    private String password;

    @NotNull(message = "role must not be null.")
    private AuthorityConstants role;

    public String toString() {
        return "UserDTO(id=" + this.getId() + ", username=" + this.getUsername() + ", firstName=" + this.getFirstName() + ", lastName=" + this.getLastName() + ", role=" + this.getRole() + ")";
    }
}