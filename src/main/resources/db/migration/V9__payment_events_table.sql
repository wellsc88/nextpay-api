CREATE TABLE payment_events (

    id UUID PRIMARY KEY,

    payment_id UUID NOT NULL,

    event_type VARCHAR(50) NOT NULL,

    description VARCHAR(255),

    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_payment_event_payment
        FOREIGN KEY (payment_id)
            REFERENCES payment(id)
);