package org.bank.customerservice.service;


import org.bank.customerservice.dto.request.CreateCustomerRequest;
import org.bank.customerservice.dto.response.CustomerResponse;

import java.util.List;

public interface CustomerService {

    CustomerResponse createCustomer(Long userId, CreateCustomerRequest request);

    CustomerResponse getByUserId(Long userId);

    CustomerResponse getCurrentCustomerProfile(Long userId);

    CustomerResponse updateCustomer(Long userId, CreateCustomerRequest request);

    void deleteCustomer(Long userId);

    List<CustomerResponse> searchCustomers(String key);

}
