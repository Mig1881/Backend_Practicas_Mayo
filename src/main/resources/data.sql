-- Administradora 1: Natalia (Password: 1111)
INSERT INTO customers (name, password, email, phone, role, age, advertising, registration_date)
SELECT 'Natalia', '$2a$12$s8eN/3nrPPaEOPc6xXaeI.k6hJEpOozrOwslpFlKsVzmggkXWiEQ2', 'natalia@kebapi.com', '+34 622333444', 'ADMIN', 27, true, '2026-05-12'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE email = 'natalia@kebapi.com');
-- Administrador 2: Nestor (Password: 2222)
INSERT INTO customers (name, password, email, phone, role, age, advertising, registration_date)
SELECT 'Nestor', '$2a$12$5V9BRO9AMnl3ZWCvueSL1OfCsECDiv5tvMDTWtotQjK06uDSCRTLa', 'nestor@kebapi.com', '+34 633444555', 'ADMIN', 28, false, '2026-05-12'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE email = 'nestor@kebapi.com');
-- Administrador 3: Miguel (Password: 3333)
INSERT INTO customers (name, password, email, phone, role, age, advertising, registration_date)
SELECT 'Miguel', '$2a$12$rPmpdrYqUGgxYQCmnYCtBenqA1bMDxbVJlF33vXUKbBuFaflDXYbi', 'miguel@kebapi.com', '+34 976112233', 'ADMIN', 54, false, '2026-05-12'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE email = 'miguel@kebapi.com');