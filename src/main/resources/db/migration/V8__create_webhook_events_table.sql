CREATE TABLE webhook_events (
    id UUID NOT NULL,
    event_id VARCHAR(100) NOT NULL,
    payment_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT pk_webhook_events
        PRIMARY KEY (id),

    CONSTRAINT uk_webhook_events_event_id
        UNIQUE (event_id)
);