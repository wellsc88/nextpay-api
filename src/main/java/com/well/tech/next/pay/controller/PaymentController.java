package com.well.tech.next.pay.controller;

import com.well.tech.next.pay.common.exceptions.ApiError;
import com.well.tech.next.pay.dto.request.payment.CreatePaymentRequest;
import com.well.tech.next.pay.dto.request.payment.PaymentFilterRequest;
import com.well.tech.next.pay.dto.request.payment.UpdatePaymentRequest;
import com.well.tech.next.pay.dto.request.payment.UpdatePaymentStatusRequest;
import com.well.tech.next.pay.dto.response.payment.PaymentEventResponse;
import com.well.tech.next.pay.dto.response.payment.PaymentResponse;
import com.well.tech.next.pay.dto.response.payment.PaymentStatusHistoryResponse;
import com.well.tech.next.pay.service.PaymentEventService;
import com.well.tech.next.pay.service.PaymentService;
import com.well.tech.next.pay.service.PaymentStatusHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.well.tech.next.pay.config.ApiVersion.API_BASE_PATH;
import static com.well.tech.next.pay.config.ApiVersion.API_VERSION;

@RestController
@RequestMapping(API_BASE_PATH + "/" + API_VERSION + "/payments")
@RequiredArgsConstructor
@Tag(
        name = "Payments",
        description = "Payment processing and management endpoints"
)
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentStatusHistoryService paymentStatusHistoryService;
    private final PaymentEventService paymentEventService;

    @Operation(
            summary = "Create payment",
            description = "Creates a new payment using idempotency protection"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Payment created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid payment data"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Duplicate idempotency key"
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse create(
            @Parameter(
                    description = "Unique key to guarantee payment idempotency",
                    required = true,
                    example = "payment-123456",
                    in = ParameterIn.HEADER
            )
            @RequestHeader("Idempotency-Key")
            String idempotencyKey,

            @Valid
            @RequestBody
            CreatePaymentRequest request
    ) {
        return paymentService.create(
                idempotencyKey,
                request
        );
    }


    @Operation(
            summary = "Find payment by id",
            description = "Returns payment details by UUID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment found successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment not found"
            )
    })
    @GetMapping("/{id}")
    public PaymentResponse findById(
            @PathVariable UUID id
    ) {
        return paymentService.findById(id);
    }


    @Operation(
            summary = "List payments",
            description = "Returns payments using filters and pagination"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payments retrieved successfully"
            )
    })
    @GetMapping
    public Page<PaymentResponse> findAll(
            @ParameterObject
            PaymentFilterRequest filter,

            @ParameterObject
            Pageable pageable
    ) {
        return paymentService.findAll(
                filter,
                pageable
        );
    }


    @Operation(
            summary = "Update payment",
            description = "Updates payment information"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment updated successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment not found"
            )
    })
    @PatchMapping("/{id}")
    public PaymentResponse patch(
            @PathVariable UUID id,

            @Valid
            @RequestBody UpdatePaymentRequest request
    ) {
        return paymentService.update(id, request);
    }


    @Operation(
            summary = "Delete payment",
            description = "Deletes a payment by UUID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Payment deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment not found"
            )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID id
    ) {
        paymentService.delete(id);
    }


    @Operation(
            summary = "Update payment status",
            description = "Changes payment status following allowed transitions"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment status updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid status transition"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment not found"
            )
    })
    @PatchMapping("/{id}/status")
    public PaymentResponse updateStatus(
            @PathVariable UUID id,

            @Valid
            @RequestBody UpdatePaymentStatusRequest request
    ) {
        return paymentService.updateStatus(
                id,
                request.status()
        );
    }


    @Operation(
            summary = "Get payment status history",
            description = "Returns all status changes performed on a payment"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Status history retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment not found"
            )
    })
    @GetMapping("/{paymentId}/status-history")
    public List<PaymentStatusHistoryResponse> getStatusHistory(
            @PathVariable UUID paymentId
    ) {
        return paymentStatusHistoryService
                .findByPaymentId(paymentId);
    }


    @Operation(
            summary = "Cancel payment",
            description = "Cancels a payment"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Payment cancelled successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payment cannot be cancelled"
            )
    })
    @PostMapping("/{paymentId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(
            @PathVariable UUID paymentId
    ) {
        paymentService.cancel(paymentId);
    }


    @Operation(
            summary = "Refund payment",
            description = "Refunds an approved payment"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Payment refunded successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payment cannot be refunded"
            )
    })
    @PostMapping("/{paymentId}/refund")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void refund(
            @PathVariable UUID paymentId
    ) throws InterruptedException {
        paymentService.refund(paymentId);
    }


    @Operation(
            summary = "Retry payment",
            description = "Creates a retry attempt for a failed payment"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Retry payment created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payment cannot be retried"
            )
    })
    @PostMapping("/{paymentId}/retry")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse retry(
            @PathVariable UUID paymentId
    ) {
        return paymentService.retry(paymentId);
    }

    @Operation(
            summary = "Get payment events",
            description = "Returns the audit events associated with a payment."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment events retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment not found",
                    content = @Content(
                            schema = @Schema(implementation = ApiError.class)
                    )
            )
    })
    @GetMapping("/{paymentId}/events")
    public List<PaymentEventResponse> getEvents(
            @Parameter(
                    description = "Payment unique identifier",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID paymentId
    ) {

        return paymentEventService.findByPaymentId(paymentId);
    }

    @Operation(
            summary = "Expire payment",
            description = "Marks a pending payment as expired."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Payment expired successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment not found"
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid payment status transition"
            )
    })
    @PostMapping("/{paymentId}/expire")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void expire(
            @Parameter(
                    description = "Payment unique identifier",
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    required = true
            )
            @PathVariable UUID paymentId
    ) {
        paymentService.expire(paymentId);
    }

    @GetMapping("/reference/{reference}")
    @Operation(
            summary = "Find payment by reference",
            description = "Retrieves a payment using its unique reference"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment found"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public PaymentResponse findByReference(
            @PathVariable String reference
    ) {
        return paymentService.findByReference(reference);
    }
}