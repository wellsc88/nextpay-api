package com.well.tech.next.pay.mapper;

import com.well.tech.next.pay.dto.request.customer.CreateCustomerRequest;
import com.well.tech.next.pay.dto.request.customer.UpdateCustomerRequest;
import com.well.tech.next.pay.dto.response.customer.CustomerResponse;
import com.well.tech.next.pay.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerMapperTest {

    private CustomerMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CustomerMapper();
    }

    @Test
    void shouldConvertCreateCustomerRequestToEntity() {

        CreateCustomerRequest request = new CreateCustomerRequest(
                "Wellington",
                "well@test.com",
                "12345678900"
        );

        Customer customer = mapper.toEntity(request);

        assertThat(customer.getName())
                .isEqualTo("Wellington");

        assertThat(customer.getEmail())
                .isEqualTo("well@test.com");

        assertThat(customer.getDocument())
                .isEqualTo("12345678900");

        assertThat(customer.getId())
                .isNull();
    }

    @Test
    void shouldUpdateCustomerEntityFields() {

        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .name("Old Name")
                .email("old@test.com")
                .document("11111111111")
                .build();

        UpdateCustomerRequest request = new UpdateCustomerRequest(
                "New Name",
                "new@test.com",
                "22222222222"
        );

        mapper.updateEntity(customer, request);

        assertThat(customer.getName())
                .isEqualTo("New Name");

        assertThat(customer.getEmail())
                .isEqualTo("new@test.com");

        assertThat(customer.getDocument())
                .isEqualTo("22222222222");

        assertThat(customer.getId())
                .isNotNull();
    }

    @Test
    void shouldConvertCustomerEntityToResponse() {

        UUID id = UUID.randomUUID();

        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        Customer customer = Customer.builder()
                .id(id)
                .name("Wellington")
                .email("well@test.com")
                .document("12345678900")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        CustomerResponse response = mapper.toResponse(customer);

        assertThat(response.id())
                .isEqualTo(id);

        assertThat(response.name())
                .isEqualTo("Wellington");

        assertThat(response.email())
                .isEqualTo("well@test.com");

        assertThat(response.document())
                .isEqualTo("12345678900");

        assertThat(response.createdAt())
                .isEqualTo(createdAt);

        assertThat(response.updatedAt())
                .isEqualTo(updatedAt);
    }
}