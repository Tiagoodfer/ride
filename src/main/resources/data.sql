-- Passenger
INSERT INTO users (id, name, email, cpf, password_hash, image_url, phone_number, created_at, status, balance)
VALUES ('123e4567-e89b-12d3-a456-426614174000', 'Passenger User', 'passenger@example.com', '111.111.111-11', 'hash123', 'http://img.com/1', '+5511999999999', NOW(), 'ACTIVE', 0.00)
ON CONFLICT (id) DO NOTHING;

INSERT INTO user_roles (user_id, role) VALUES ('123e4567-e89b-12d3-a456-426614174000', 'PASSENGER')
ON CONFLICT DO NOTHING;

-- Driver
INSERT INTO users (id, name, email, cpf, password_hash, image_url, phone_number, created_at, status, balance)
VALUES ('123e4567-e89b-12d3-a456-426614174001', 'Driver User', 'driver@example.com', '222.222.222-22', 'hash123', 'http://img.com/2', '+5511888888888', NOW(), 'ACTIVE', 0.00)
ON CONFLICT (id) DO NOTHING;

INSERT INTO user_roles (user_id, role) VALUES ('123e4567-e89b-12d3-a456-426614174001', 'DRIVER')
ON CONFLICT DO NOTHING;

-- Influencer
INSERT INTO users (id, name, email, cpf, password_hash, image_url, phone_number, created_at, status, balance)
VALUES ('123e4567-e89b-12d3-a456-426614174002', 'Influencer User', 'influencer@example.com', '333.333.333-33', 'hash123', 'http://img.com/3', '+5511777777777', NOW(), 'ACTIVE', 0.00)
ON CONFLICT (id) DO NOTHING;

INSERT INTO user_roles (user_id, role) VALUES ('123e4567-e89b-12d3-a456-426614174002', 'INFLUENCER')
ON CONFLICT DO NOTHING;

-- Admin
INSERT INTO users (id, name, email, cpf, password_hash, image_url, phone_number, created_at, status, balance)
VALUES ('123e4567-e89b-12d3-a456-426614174003', 'Admin User', 'admin@example.com', '444.444.444-44', 'hash123', 'http://img.com/4', '+5511666666666', NOW(), 'ACTIVE', 0.00)
ON CONFLICT (id) DO NOTHING;

INSERT INTO user_roles (user_id, role) VALUES ('123e4567-e89b-12d3-a456-426614174003', 'ADMIN')
ON CONFLICT DO NOTHING;
