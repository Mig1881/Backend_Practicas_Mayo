INSERT INTO customers (name, password, email, phone, role, age, advertising, registration_date)
SELECT 'admin', '1881', 'admin@practicas.com', '+34 987654321', 'ADMIN', 54,false, '2026-01-30'
    WHERE NOT EXISTS (SELECT 1 FROM customers WHERE name = 'admin');