package com.well.tech.next.pay.service;

import com.well.tech.next.pay.common.exceptions.resource.ResourceNotFoundException;
import com.well.tech.next.pay.dto.request.customer.CreateCustomerRequest;
import com.well.tech.next.pay.dto.request.customer.CustomerFilterRequest;
import com.well.tech.next.pay.dto.request.customer.UpdateCustomerRequest;
import com.well.tech.next.pay.dto.response.customer.CustomerResponse;
import com.well.tech.next.pay.entity.Customer;
import com.well.tech.next.pay.mapper.CustomerMapper;
import com.well.tech.next.pay.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;

    private CustomerResponse response;

    @BeforeEach
    void setup() {

        MockitoAnnotations.openMocks(this);

        customer =
                new Customer();

        customer.setId(
                UUID.randomUUID()
        );

        response =
                mock(CustomerResponse.class);
    }

    @Test
    void shouldCreateCustomerSuccessfully() {

        CreateCustomerRequest request =
                mock(CreateCustomerRequest.class);

        when(customerMapper.toEntity(request))
                .thenReturn(customer);

        when(customerRepository.save(customer))
                .thenReturn(customer);

        when(customerMapper.toResponse(customer))
                .thenReturn(response);

        CustomerResponse result =
                customerService.create(request);

        assertNotNull(result);

        verify(customerMapper)
                .toEntity(request);

        verify(customerRepository)
                .save(customer);

        verify(customerMapper)
                .toResponse(customer);
    }

    @Test
    void shouldFindAllCustomersWithPaginationSuccessfully() {

        CustomerFilterRequest filter =
                mock(CustomerFilterRequest.class);

        PageRequest pageable =
                PageRequest.of(0,10);

        Page<Customer> page =
                new PageImpl<>(
                        List.of(customer)
                );

        when(customerRepository.findAll(
                ArgumentMatchers.<Specification<Customer>>any(),
                eq(pageable)
        )).thenReturn(page);
        when(customerMapper.toResponse(customer))
                .thenReturn(response);

        Page<CustomerResponse> result =
                customerService.findAll(
                        filter,
                        pageable
                );

        assertEquals(
                1,
                result.getTotalElements()
        );

        verify(customerRepository)
                .findAll(
                        ArgumentMatchers.<Specification<Customer>>any(),
                        eq(pageable)
                );
    }

    @Test
    void shouldFindAllCustomersSuccessfully() {

        when(customerRepository.findAll())
                .thenReturn(
                        List.of(customer)
                );

        when(customerMapper.toResponse(customer))
                .thenReturn(response);

        List<CustomerResponse> result =
                customerService.findAll();

        assertEquals(
                1,
                result.size()
        );

        verify(customerRepository)
                .findAll();
    }

    @Test
    void shouldUpdateCustomerSuccessfully() {

        UUID id =  customer.getId();

        UpdateCustomerRequest request =
                mock(UpdateCustomerRequest.class);

        when(customerRepository.findById(id))
                .thenReturn(
                        Optional.of(customer)
                );

        when(customerMapper.toResponse(customer))
                .thenReturn(response);

        CustomerResponse result =
                customerService.update(
                        id,
                        request
                );

        assertNotNull(result);

        verify(customerMapper)
                .updateEntity(
                        customer,
                        request
                );

        verify(customerMapper)
                .toResponse(customer);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingCustomerNotFound() {

        UUID id = UUID.randomUUID();

        UpdateCustomerRequest request =
                mock(UpdateCustomerRequest.class);

        when(customerRepository.findById(id))
                .thenReturn(
                        Optional.empty()
                );

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () ->
                                customerService.update(
                                        id,
                                        request
                                )
                );

        assertEquals(
                "Customer not found",
                exception.getMessage()
        );

        verify(customerMapper, never())
                .updateEntity(
                        any(),
                        any()
                );
    }

    @Test
    void shouldDeleteCustomerSuccessfully() {

        UUID id =
                customer.getId();

        when(customerRepository.existsById(id))
                .thenReturn(true);

        customerService.delete(id);

        verify(customerRepository)
                .existsById(id);

        verify(customerRepository)
                .deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenDeletingCustomerNotFound() {

        UUID id =
                UUID.randomUUID();

        when(customerRepository.existsById(id))
                .thenReturn(false);

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () ->
                                customerService.delete(id)
                );

        assertEquals(
                "Customer not found",
                exception.getMessage()
        );

        verify(customerRepository, never())
                .deleteById(id);
    }
}