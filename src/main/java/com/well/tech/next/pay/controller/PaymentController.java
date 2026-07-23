package com.well.tech.next.pay.controller;

import com.well.tech.next.pay.dto.request.payment.CreatePaymentRequest;
import com.well.tech.next.pay.dto.request.payment.PaymentFilterRequest;
import com.well.tech.next.pay.dto.request.payment.UpdatePaymentStatusRequest;
import com.well.tech.next.pay.dto.response.payment.PaymentResponse;
import com.well.tech.next.pay.dto.request.payment.UpdatePaymentRequest;
import com.well.tech.next.pay.dto.response.payment.PaymentStatusHistoryResponse;
import com.well.tech.next.pay.service.PaymentService;
import com.well.tech.next.pay.service.PaymentStatusHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentStatusHistoryService paymentStatusHistoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse create(
            @RequestHeader("Idempotency-Key")
            String idempotencyKey,

            @RequestBody
            CreatePaymentRequest request
    ) {
        return paymentService.create(
                idempotencyKey,
                request
        );
    }

    @GetMapping("/{id}")
    public PaymentResponse findById(
            @PathVariable UUID id
    ) {
        return paymentService.findById(id);
    }

    @GetMapping
    public Page<PaymentResponse> findAll(
            PaymentFilterRequest filter,
            Pageable pageable
    ) {
        return paymentService.findAll(
                filter,
                pageable
        );
    }

    @PatchMapping("/{id}")
    public PaymentResponse patch(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePaymentRequest request
    ) {
        return paymentService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID id
    ) {
        paymentService.delete(id);
    }

    @PatchMapping("/{id}/status")
    public PaymentResponse updateStatus(
            @PathVariable UUID id,
            @RequestBody UpdatePaymentStatusRequest request
    ) {
        return paymentService.updateStatus(
                id,
                request.status()
        );
    }

    @GetMapping("/{paymentId}/status-history")
    public List<PaymentStatusHistoryResponse> getStatusHistory(
            @PathVariable UUID paymentId
    ) {
        return paymentStatusHistoryService
                .findByPaymentId(paymentId);
    }
}