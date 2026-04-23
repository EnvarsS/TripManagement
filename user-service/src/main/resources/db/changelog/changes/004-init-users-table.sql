-- Liquibase formatted SQL
-- changeset envars:004

INSERT INTO users (name, email, hashed_password, role_id) VALUES
(
    'Admin1',
    'admin@example.com',
    '$2a$10$8IqjvC/gu6BlsDhqIs2TKu8I7sYUHtK0q/VZNReNlx0s0EYt0jQhK',
    (SELECT id FROM roles WHERE name = 'ADMIN')
),
(
    'User2',
    'user2@example.com',
    '$2a$10$DXwDap14b0IOoXRiNIzga.W5Ih4bZyOClPxc6SXQp0KYYBXJZMFjm',
    (SELECT id FROM roles WHERE name = 'USER')
),
(
    'User3',
    'user3@example.com',
    '$2a$10$0aiUr0yZuch2wSBfvqX8SOTDEe0iLj/jHPIDeR3OJkiJ84yGQTeV2',
    (SELECT id FROM roles WHERE name = 'USER')
),
(
    'User4',
    'user4@example.com',
    '$2a$10$lWeU9TJzbpSFSs06tZ9kSO/SZBwf0Z1X73YF9aVeyIQICSaB62lZW',
    (SELECT id FROM roles WHERE name = 'USER')
),
(
    'User5',
    'user5@example.com',
    '$2a$10$HzzoLm2bcMutn1IQ/9KNbehAKeWU1stUEX423POCHFKMhiyg80pm.',
    (SELECT id FROM roles WHERE name = 'USER')
);