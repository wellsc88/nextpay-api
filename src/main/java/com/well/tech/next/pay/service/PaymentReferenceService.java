package com.well.tech.next.pay.service;

import com.well.tech.next.pay.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentReferenceService {

    private final PaymentRepository paymentRepository;

    public String generateReference() {

        while (true) {

            String reference = buildReference();

            if (!paymentRepository.existsByReference(reference)) {

                log.debug(
                        "Generated payment reference. reference={}",
                        reference
                );

                return reference;
            }

            log.warn(
                    "Duplicate payment reference generated. Retrying. reference={}",
                    reference
            );
        }
    }

    private String buildReference() {

        String date = LocalDate.now()
                .format(DateTimeFormatter.BASIC_ISO_DATE);

        String suffix = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();

        return "PAY-" + date + "-" + suffix;
    }
}