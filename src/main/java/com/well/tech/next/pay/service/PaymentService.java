package com.well.tech.next.pay.service;

import com.well.tech.next.pay.common.exceptions.resource.ResourceNotFoundException;
import com.well.tech.next.pay.dto.request.payment.CreatePaymentRequest;
import com.well.tech.next.pay.dto.request.payment.UpdatePaymentRequest;
import com.well.tech.next.pay.dto.response.payment.PaymentResponse;
import com.well.tech.next.pay.entity.Customer;
import com.well.tech.next.pay.entity.Payment;
import com.well.tech.next.pay.mapper.PaymentMapper;
import com.well.tech.next.pay.repository.CustomerRepository;
import com.well.tech.next.pay.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final PaymentMapper paymentMapper;

    @Transactional
    public PaymentResponse create(CreatePaymentRequest request) {

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Payment payment = paymentMapper.toEntity(request, customer);

        Payment savedPayment = paymentRepository.save(payment);

        return paymentMapper.toResponse(savedPayment);
    }

    @Transactional
    public PaymentResponse update(
            UUID id,
            UpdatePaymentRequest request
    ) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        paymentMapper.updateEntity(payment, request);

        return paymentMapper.toResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse findById(UUID id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        return paymentMapper.toResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> findAll() {

        return paymentRepository.findAll()
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Transactional
    public void delete(UUID id) {

        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payment not found");
        }

        paymentRepository.deleteById(id);
    }
}