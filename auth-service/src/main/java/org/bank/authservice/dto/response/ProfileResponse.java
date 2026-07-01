package org.bank.authservice.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bank.authservice.entity.Role;

@Getter
@AllArgsConstructor
public class ProfileResponse {

    private Long id;
    private String username;
    private Role role;
}
