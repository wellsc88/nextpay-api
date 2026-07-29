package com.well.tech.next.pay.controller;

import com.well.tech.next.pay.dto.request.customer.CreateCustomerRequest;
import com.well.tech.next.pay.dto.request.customer.CustomerFilterRequest;
import com.well.tech.next.pay.dto.request.customer.UpdateCustomerRequest;
import com.well.tech.next.pay.dto.response.customer.CustomerResponse;
import com.well.tech.next.pay.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.well.tech.next.pay.config.ApiVersion.API_BASE_PATH;
import static com.well.tech.next.pay.config.ApiVersion.API_VERSION;

@RestController
@RequestMapping(API_BASE_PATH + "/" + API_VERSION + "/customers")
@RequiredArgsConstructor
@Tag(
        name = "Customers",
        description = "Customer management endpoints"
)
public class CustomerController {

    private final CustomerService customerService;


    @Operation(
            summary = "Create customer",
            description = "Creates a new customer account"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Customer created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid customer data"
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(
            @Valid @RequestBody CreateCustomerRequest request
    ) {
        return customerService.create(request);
    }


    @Operation(
            summary = "List customers",
            description = "Returns customers with optional filters and pagination"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Customers retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid filter parameters"
            )
    })
    @GetMapping
    public Page<CustomerResponse> findAll(
            @ModelAttribute CustomerFilterRequest filter,
            Pageable pageable
    ) {
        return customerService.findAll(
                filter,
                pageable
        );
    }


    @Operation(
            summary = "Update customer",
            description = "Updates customer information partially"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid customer data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found"
            )
    })
    @PatchMapping("/{id}")
    public CustomerResponse patch(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerRequest request
    ) {
        return customerService.update(id, request);
    }


    @Operation(
            summary = "Delete customer",
            description = "Removes a customer by UUID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Customer deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found"
            )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID id
    ) {
        customerService.delete(id);
    }
}