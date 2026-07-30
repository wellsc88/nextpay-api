package com.well.tech.next.pay.mapper;

import com.well.tech.next.pay.dto.response.payment.PaymentEventResponse;
import com.well.tech.next.pay.entity.PaymentEvent;

public final class PaymentEventMapper {

    private PaymentEventMapper(){}

    public static PaymentEventResponse from(
            PaymentEvent event
    ) {

        return new PaymentEventResponse(

                event.getId(),

                event.getEventType(),

                event.getDescription(),

                event.getCreatedAt()
        );
    }

}