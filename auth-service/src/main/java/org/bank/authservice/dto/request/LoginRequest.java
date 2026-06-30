package org.bank.authservice.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "username is required")
    @Size(min = 4, max = 50, message = "Username must be between 4 and 50")
    private String username;
    @NotBlank(message = "password is required")
    @Size(min = 8,max = 100,message = "password must be between 8 and 100")
    private String password;
}
