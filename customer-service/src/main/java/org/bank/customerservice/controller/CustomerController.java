package org.bank.customerservice.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bank.customerservice.dto.request.CreateCustomerRequest;
import org.bank.customerservice.dto.response.CustomerResponse;
import org.bank.customerservice.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public CustomerResponse createCustomer(
            @RequestHeader("X-User-Id") Long userId,
           @Valid @RequestBody CreateCustomerRequest request
    ) {
        return customerService.createCustomer(userId, request);
    }

    @GetMapping("/me")
    public CustomerResponse getMyProfile(
            @RequestHeader("X-User-Id") Long userId
    ) {
        return customerService.getCurrentCustomerProfile(userId);
    }

    @PutMapping("/me")
    public CustomerResponse updateMyProfile(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateCustomerRequest request) {
        return customerService.updateCustomer(userId, request);
    }

    @DeleteMapping("/me")
    public void deleteMyProfile(
            @Valid @RequestHeader("X-User-Id") long userId
    ) {
        customerService.deleteCustomer(userId);
    }

    @GetMapping("/search")
    public List<CustomerResponse> search(
            @RequestParam String key
    ) {
        return customerService.searchCustomers(key);
    }
}
