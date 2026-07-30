package com.well.tech.next.pay.service;

import com.well.tech.next.pay.common.exceptions.validation.PaymentNotFoundException;
import com.well.tech.next.pay.dto.response.payment.PaymentTimelineResponse;
import com.well.tech.next.pay.entity.PaymentEvent;
import com.well.tech.next.pay.entity.PaymentStatusHistory;
import com.well.tech.next.pay.repository.PaymentEventRepository;
import com.well.tech.next.pay.repository.PaymentRepository;
import com.well.tech.next.pay.repository.PaymentStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentTimelineService {

    private final PaymentEventRepository paymentEventRepository;
    private final PaymentStatusHistoryRepository paymentStatusHistoryRepository;
    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public List<PaymentTimelineResponse> findTimeline(UUID paymentId) {

        log.info(
                "Finding payment timeline. paymentId={}",
                paymentId
        );

        paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new PaymentNotFoundException(paymentId));

        List<PaymentEvent> events =
                paymentEventRepository.findByPaymentIdOrderByCreatedAtAsc(
                        paymentId
                );

        List<PaymentStatusHistory> statusHistory =
                paymentStatusHistoryRepository.findByPaymentIdOrderByCreatedAtAsc(
                        paymentId
                );

        List<PaymentTimelineResponse> timeline = Stream.concat(
                        events.stream().map(this::fromEvent),
                        statusHistory.stream().map(this::fromStatus)
                )
                .sorted(
                        Comparator.comparing(
                                PaymentTimelineResponse::createdAt
                        )
                )
                .collect(Collectors.toList());

        log.info(
                "Payment timeline retrieved successfully. paymentId={}, entries={}",
                paymentId,
                timeline.size()
        );

        return timeline;
    }

    private PaymentTimelineResponse fromEvent(PaymentEvent event) {

        return new PaymentTimelineResponse(
                "EVENT",
                event.getDescription(),
                event.getCreatedAt()
        );
    }

    private PaymentTimelineResponse fromStatus(
            PaymentStatusHistory history
    ) {

        return new PaymentTimelineResponse(
                "STATUS",
                String.format(
                        "Status changed from %s to %s",
                        history.getFromStatus(),
                        history.getToStatus()
                ),
                history.getCreatedAt()
        );
    }
}