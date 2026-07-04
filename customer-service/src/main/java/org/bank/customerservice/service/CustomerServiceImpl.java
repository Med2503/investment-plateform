package org.bank.customerservice.service;


import lombok.RequiredArgsConstructor;
import org.bank.customerservice.dto.request.CreateCustomerRequest;
import org.bank.customerservice.dto.response.CustomerResponse;
import org.bank.customerservice.entity.Customer;
import org.bank.customerservice.exception.CustomerAlreadyExistException;
import org.bank.customerservice.exception.CustomerNotFoundException;
import org.bank.customerservice.mapper.CustomerMapper;
import org.bank.customerservice.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {


    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;


    @Override
    public CustomerResponse createCustomer(Long userId, CreateCustomerRequest request) {
        if (customerRepository.existsByUserId(userId)) {
            throw new CustomerAlreadyExistException("Customer exits for this userId");
        }

        Customer customer = customerMapper.toEntity(request, userId);

        return customerMapper.toResponse(
                customerRepository.save(customer)
        );
    }

    @Override
    public CustomerResponse getByUserId(Long userId) {
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        return customerMapper.toResponse(customer);
    }

    @Override
    public CustomerResponse getCurrentCustomerProfile(Long userId) {
        return getByUserId(userId);
    }

    @Override
    public CustomerResponse updateCustomer(Long userId, CreateCustomerRequest request) {
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setNationality(request.getNationality());
        customer.setAddress(request.getAddress());
        customer.setCity(request.getCity());
        customer.setCountry(request.getCountry());

        return customerMapper.toResponse(
                customerRepository.save(customer)
        );
    }

    @Override
    public void deleteCustomer(Long userId) {
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer don't exits"));
        customerRepository.delete(customer);
    }

    @Override
    public List<CustomerResponse> searchCustomers(String key) {
        List<Customer> customers = customerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                key,
                key
        );
        return customers.stream()
                .map(customerMapper::toResponse)
                .toList();
    }
}
