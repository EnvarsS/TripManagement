-- Liquibase formatted SQL
-- changeset envars:004

INSERT INTO users (name, email, hashed_password, role_id) VALUES
(
    'Admin',
    'admin@example.com',
    'pass1',
    (SELECT id FROM roles WHERE name = 'ADMIN')
),
(
    'User1',
    'user1@example.com',
    'pass2',
    (SELECT id FROM roles WHERE name = 'USER')
),
(
    'User2',
    'user2@example.com',
    'pass3',
    (SELECT id FROM roles WHERE name = 'USER')
),
(
    'User3',
    'user3@example.com',
    'pass4',
    (SELECT id FROM roles WHERE name = 'USER')
),
(
    'User4',
    'user4@example.com',
    'pass5',
    (SELECT id FROM roles WHERE name = 'USER')
);