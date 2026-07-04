package org.bank.customerservice.dto.response;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class CustomerResponse {

    private Long id;
    private Long userId;

    private String firstName;
    private String lastName;
    private String email;

    private String phoneNumber;
    private LocalDate dateOfBirth;

    private String nationality;
    private String address;
    private String city;
    private String country;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
