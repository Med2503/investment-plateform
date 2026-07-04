package org.bank.customerservice.repository;

import org.bank.customerservice.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByUserId(Long userId);

    Optional<Customer> findByEmail(String email);

    boolean existsByUserId(Long userId);

    boolean existsByEmail(String email);

}
