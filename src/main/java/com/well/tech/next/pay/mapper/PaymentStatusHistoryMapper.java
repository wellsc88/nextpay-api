package com.well.tech.next.pay.mapper;

import com.well.tech.next.pay.dto.response.payment.PaymentStatusHistoryResponse;
import com.well.tech.next.pay.entity.PaymentStatusHistory;
import org.springframework.stereotype.Component;

@Component
public class PaymentStatusHistoryMapper {

    public PaymentStatusHistoryResponse toResponse(
            PaymentStatusHistory history
    ) {
        return new PaymentStatusHistoryResponse(
                history.getId(),
                history.getFromStatus(),
                history.getToStatus(),
                history.getCreatedAt()
        );
    }
}