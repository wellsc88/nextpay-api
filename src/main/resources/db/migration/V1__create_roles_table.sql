CREATE TABLE roles (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

INSERT INTO roles (id, name, description)
VALUES
    (
        gen_random_uuid(),
        'USER',
        'Default application user'
    ),
    (
        gen_random_uuid(),
        'ADMIN',
        'System administrator'
    );