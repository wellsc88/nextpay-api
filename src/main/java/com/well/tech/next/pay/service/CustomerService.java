package com.well.tech.next.pay.service;

import com.well.tech.next.pay.common.exceptions.resource.ResourceNotFoundException;
import com.well.tech.next.pay.dto.request.customer.CreateCustomerRequest;
import com.well.tech.next.pay.dto.request.customer.UpdateCustomerRequest;
import com.well.tech.next.pay.dto.response.customer.CustomerResponse;
import com.well.tech.next.pay.entity.Customer;
import com.well.tech.next.pay.mapper.CustomerMapper;
import com.well.tech.next.pay.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Transactional
    public CustomerResponse create(CreateCustomerRequest request) {

        log.info("Creating customer with email: {}", request.email());

        Customer customer = customerMapper.toEntity(request);

        Customer savedCustomer = customerRepository.save(customer);

        log.info("Customer created successfully with id: {}", savedCustomer.getId());

        return customerMapper.toResponse(savedCustomer);
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(UUID id) {

        log.info("Finding customer by id: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Customer not found with id: {}", id);
                    return new ResourceNotFoundException("Customer not found");
                });

        return customerMapper.toResponse(customer);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll() {

        log.info("Finding all customers");

        List<CustomerResponse> customers = customerRepository.findAll()
                .stream()
                .map(customerMapper::toResponse)
                .toList();

        log.info("Found {} customers", customers.size());

        return customers;
    }

    @Transactional
    public CustomerResponse update(
            UUID id,
            UpdateCustomerRequest request
    ) {

        log.info("Updating customer with id: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Customer not found with the id: {}", id);
                    return new ResourceNotFoundException("Customer not found");
                });

        customerMapper.updateEntity(customer, request);

        log.info("Customer updated successfully with id: {}", id);

        return customerMapper.toResponse(customer);
    }

    @Transactional
    public void delete(UUID id) {

        log.info("Deleting customer with id: {}", id);

        if (!customerRepository.existsById(id)) {
            log.warn("Customer not found with id: {}", id);
            throw new ResourceNotFoundException("Customer not found");
        }

        customerRepository.deleteById(id);

        log.info("Customer deleted successfully with id: {}", id);
    }
}