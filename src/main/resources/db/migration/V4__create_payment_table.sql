CREATE TABLE payment (
     id UUID PRIMARY KEY,
     customer_id UUID NOT NULL,
     parent_payment_id UUID,
     idempotency_key VARCHAR(255) NOT NULL,
     amount NUMERIC(19, 2) NOT NULL,
     currency VARCHAR(3) NOT NULL,
     status VARCHAR(20) NOT NULL,
     payment_method VARCHAR(20) NOT NULL,
     description VARCHAR(255),
     reference VARCHAR(30) NOT NULL UNIQUE,
     created_at TIMESTAMP NOT NULL,
     updated_at TIMESTAMP NOT NULL,
     expires_at TIMESTAMP,

     CONSTRAINT uk_payment_idempotency_key
         UNIQUE (idempotency_key),

     CONSTRAINT fk_payment_customer
         FOREIGN KEY (customer_id)
             REFERENCES customer(id),

     CONSTRAINT fk_payment_parent
         FOREIGN KEY (parent_payment_id)
             REFERENCES payment(id),

     CONSTRAINT uk_payments_reference
         UNIQUE(reference)
);