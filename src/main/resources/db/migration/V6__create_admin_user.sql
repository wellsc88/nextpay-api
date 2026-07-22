INSERT INTO users (
    id,
    name,
    email,
    password,
    role_id,
    enabled,
    created_at,
    updated_at
)
VALUES (
   '839d2c44-433e-466d-9d5b-1a54f77755a5',
   'Administrator',
   'admin@nextpay.com',
   '$2a$12$k7mXa9FZXWuVpshVHoBYjuQ9MEvMEMz5tnzxJA5R1DGa72CB9huIK', --admin@123
   (SELECT id FROM roles WHERE name = 'ADMIN'),
   true,
   CURRENT_TIMESTAMP,
   CURRENT_TIMESTAMP
);