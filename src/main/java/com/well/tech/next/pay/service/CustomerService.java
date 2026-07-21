package com.well.tech.next.pay.service;

import com.well.tech.next.pay.common.exceptions.resource.ResourceNotFoundException;
import com.well.tech.next.pay.dto.request.customer.CreateCustomerRequest;
import com.well.tech.next.pay.dto.request.customer.UpdateCustomerRequest;
import com.well.tech.next.pay.dto.response.customer.CustomerResponse;
import com.well.tech.next.pay.entity.Customer;
import com.well.tech.next.pay.mapper.CustomerMapper;
import com.well.tech.next.pay.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Transactional
    public CustomerResponse create(CreateCustomerRequest request) {

        Customer customer = customerMapper.toEntity(request);

        Customer savedCustomer = customerRepository.save(customer);

        return customerMapper.toResponse(savedCustomer);
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(UUID id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        return customerMapper.toResponse(customer);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll() {

        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    @Transactional
    public CustomerResponse update(
            UUID id,
            UpdateCustomerRequest request
    ) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        customerMapper.updateEntity(customer, request);

        return customerMapper.toResponse(customer);
    }

    @Transactional
    public void delete(UUID id) {

        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found");
        }

        customerRepository.deleteById(id);
    }
}