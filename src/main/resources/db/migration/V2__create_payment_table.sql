CREATE TABLE payment (
     id UUID PRIMARY KEY,
     customer_id UUID NOT NULL,
     amount NUMERIC(19, 2) NOT NULL,
     currency VARCHAR(3) NOT NULL,
     status VARCHAR(20) NOT NULL,
     payment_method VARCHAR(20) NOT NULL,
     description VARCHAR(255),
     created_at TIMESTAMP NOT NULL,
     updated_at TIMESTAMP NOT NULL,

     CONSTRAINT fk_payment_customer
         FOREIGN KEY (customer_id)
             REFERENCES customer(id)
);