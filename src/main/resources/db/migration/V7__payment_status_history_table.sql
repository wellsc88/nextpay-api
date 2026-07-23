CREATE TABLE payment_status_history (
    id UUID PRIMARY KEY,
    payment_id UUID NOT NULL,
    from_status VARCHAR(20) NOT NULL,
    to_status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_status_history_payment
        FOREIGN KEY (payment_id)
            REFERENCES payment(id)
);