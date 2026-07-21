package com.well.tech.next.pay.controller;

import com.well.tech.next.pay.dto.request.payment.CreatePaymentRequest;
import com.well.tech.next.pay.dto.response.payment.PaymentResponse;
import com.well.tech.next.pay.dto.request.payment.UpdatePaymentRequest;
import com.well.tech.next.pay.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse create(
            @Valid @RequestBody CreatePaymentRequest request
    ) {
        return paymentService.create(request);
    }

    @GetMapping("/{id}")
    public PaymentResponse findById(
            @PathVariable UUID id
    ) {
        return paymentService.findById(id);
    }

    @GetMapping
    public List<PaymentResponse> findAll() {
        return paymentService.findAll();
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
}