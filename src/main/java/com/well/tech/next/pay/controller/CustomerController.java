package com.well.tech.next.pay.controller;

import com.well.tech.next.pay.dto.request.customer.CreateCustomerRequest;
import com.well.tech.next.pay.dto.response.customer.CustomerResponse;
import com.well.tech.next.pay.dto.request.customer.UpdateCustomerRequest;
import com.well.tech.next.pay.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(
            @Valid @RequestBody CreateCustomerRequest request
    ) {
        return customerService.create(request);
    }

    @GetMapping("/{id}")
    public CustomerResponse findById(
            @PathVariable UUID id
    ) {
        return customerService.findById(id);
    }

    @GetMapping
    public List<CustomerResponse> findAll() {
        return customerService.findAll();
    }

    @PatchMapping("/{id}")
    public CustomerResponse patch(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerRequest request
    ) {
        return customerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID id
    ) {
        customerService.delete(id);
    }
}