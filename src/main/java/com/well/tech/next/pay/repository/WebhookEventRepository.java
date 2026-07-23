package com.well.tech.next.pay.repository;

import com.well.tech.next.pay.entity.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WebhookEventRepository
        extends JpaRepository<WebhookEvent, UUID> {

    boolean existsByEventId(String eventId);
}