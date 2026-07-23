package com.well.tech.next.pay.repository;

import com.well.tech.next.pay.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID>,
        JpaSpecificationExecutor<Customer> {

    boolean existsByEmail(String email);

    boolean existsByDocument(String document);
}