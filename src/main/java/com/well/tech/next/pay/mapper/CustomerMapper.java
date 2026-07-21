package com.well.tech.next.pay.mapper;

import com.well.tech.next.pay.dto.request.customer.CreateCustomerRequest;
import com.well.tech.next.pay.dto.response.customer.CustomerResponse;
import com.well.tech.next.pay.dto.request.customer.UpdateCustomerRequest;
import com.well.tech.next.pay.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toEntity(CreateCustomerRequest request) {
        return Customer.builder()
                .name(request.name())
                .email(request.email())
                .document(request.document())
                .build();
    }

    public void updateEntity(
            Customer customer,
            UpdateCustomerRequest request
    ) {
        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setDocument(request.document());
    }

    public CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getDocument(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}