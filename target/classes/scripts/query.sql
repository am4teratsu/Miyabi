CREATE DATABASE DB_Miyabi CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE DB_Miyabi;


-- TABLA 1: Roles
CREATE TABLE roles (
    rol_id INT AUTO_INCREMENT PRIMARY KEY,
    name_rol VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200)
);

-- TABLA 2: Usuarios (Admin y Recepcionistas)
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    names VARCHAR(100) NOT NULL,
    surnames VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    state TINYINT DEFAULT 1 COMMENT '1=Active, 0=Inactive',
    creation_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    rol_id INT NOT NULL,
    FOREIGN KEY (rol_id) REFERENCES roles(rol_id)
);

-- TABLA 3: Clientes
CREATE TABLE guests (
    guest_id INT AUTO_INCREMENT PRIMARY KEY,
    names VARCHAR(100) NOT NULL,
    surnames VARCHAR(100) NOT NULL,
    dni VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(15),
    address VARCHAR(200),
    registration_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    state TINYINT DEFAULT 1
);


-- TABLA 4: Tipo de habitacion
-- Categorías: Simple, Doble, Suite, Suite Doble, etc.
-- Solo el Admin puede crear/modificar estos tipos
CREATE TABLE room_type (
    type_id INT AUTO_INCREMENT PRIMARY KEY,
    name_type VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    capacity_people INT NOT NULL DEFAULT 2,
    base_price DECIMAL(10,2) NOT NULL,
    high_season_price DECIMAL(10,2),
    image_url VARCHAR(500),
    amenities TEXT COMMENT 'WiFi, TV, Jacuzzi, etc.'
);


-- TABLA 5: Habitaciones
-- Solo el Admin puede crear/modificar/bloquear habitaciones
CREATE TABLE rooms (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(10) NOT NULL UNIQUE COMMENT 'Ej: 101, 205, P3',
    floor INT NOT NULL,
    state VARCHAR(20) NOT NULL DEFAULT 'Available' 
        COMMENT 'Available, Occupied, Reserved, Cleaning, Maintenance',
    additional_description TEXT,
    date_last_maintenance DATE,
    type_id INT NOT NULL,
    FOREIGN KEY (type_id) REFERENCES room_type(type_id)
);

-- TABLA 6: Reservas
-- Estados: Pendiente → Confirmada → Check-in → Check-out/Cancelada
CREATE TABLE reservations (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    reservation_code VARCHAR(20) NOT NULL UNIQUE 
        COMMENT 'Código para buscar: RES-2026-0001',
    entry_date DATE NOT NULL,
    departure_date DATE NOT NULL,
    number_nights INT NOT NULL,
    price_per_night DECIMAL(10,2) NOT NULL COMMENT 'Precio al momento de reservar',
    room_subtotal DECIMAL(10,2) NOT NULL COMMENT 'precio_noche x noches',
    total_consumption DECIMAL(10,2) DEFAULT 0.00,
    total_pay DECIMAL(10,2) NOT NULL COMMENT 'subtotal + consumos',
    state VARCHAR(20) NOT NULL DEFAULT 'Peding'
        COMMENT 'Pending, Confirmed, Check-in, Check-out, Cancelled',
    observations TEXT,
    reservation_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    checkin_date DATETIME COMMENT 'Cuando el recep hace check-in',
    checkout_date DATETIME COMMENT 'Cuando el recep hace check-out',
    guest_id INT NOT NULL,
    room_id INT NOT NULL,
    user_id_checkin INT COMMENT 'Recepcionista que hizo check-in',
    user_id_checkout INT COMMENT 'Recepcionista que hizo check-out',
    FOREIGN KEY (guest_id) REFERENCES guests(guest_id),
    FOREIGN KEY (room_id) REFERENCES rooms(room_id),
    FOREIGN KEY (user_id_checkin) REFERENCES users(user_id),
    FOREIGN KEY (user_id_checkout) REFERENCES users(user_id)
);

-- TABLA 7: Catalogo de servicios
-- Productos/servicios que se pueden agregar a una reserva
CREATE TABLE services_catalog (
    service_id INT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(50) COMMENT 'Bebidas, Comidas, Spa, Lavandería, etc.',
    available TINYINT DEFAULT 1 COMMENT '1=Active, 0=Inactive'
);


-- TABLA 8: Consumos
-- Servicios adicionales que se cargan a la reserva durante la estancia
CREATE TABLE consumption (
    consumption_id INT AUTO_INCREMENT PRIMARY KEY,
    amount INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL COMMENT 'cantidad x precio_unitario',
    observation VARCHAR(200),
    consumption_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    reservation_id INT NOT NULL,
    service_id INT NOT NULL,
    user_registration_id INT COMMENT 'Quien registró el consumo',
    FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id),
    FOREIGN KEY (service_id) REFERENCES services_catalog(service_id),
    FOREIGN KEY (user_registration_id) REFERENCES users(user_id)
);

-- TABLA 9: Pagos
CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    total_amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(30) NOT NULL COMMENT 'Efectivo, Tarjeta, Transferencia',
    payment_status VARCHAR(20) DEFAULT 'Paid',
    receipt_number VARCHAR(50),
    payment_day DATETIME DEFAULT CURRENT_TIMESTAMP,
    observation TEXT,
    reservation_id INT NOT NULL UNIQUE,
    user_charge_id INT COMMENT 'Recepcionista que cobró',
    FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id),
    FOREIGN KEY (user_charge_id) REFERENCES users(user_id)
);

-- TABLA 10: Registros de acceso para el admin
CREATE TABLE access_log (
    access_id INT AUTO_INCREMENT PRIMARY KEY,
    access_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    access_ip VARCHAR(50),
    user_type VARCHAR(20) COMMENT 'Usuario o Cliente',
    user_id INT,
    guest_id INT
);


-- INSERCCIONES
-- ROLES

INSERT INTO roles (name_rol, description) VALUES 
('Administrador', 'Acceso total al sistema, gestión de habitaciones y usuarios'),
('Recepcionista', 'Gestión de reservas, check-in, check-out y servicios'),
('Cliente', 'Acceso web para ver y crear reservas');

-- ============================================================
-- USUARIOS (Admin y Recepcionistas)
-- Contraseñas en texto plano para pruebas
-- ============================================================
INSERT INTO users (names, surnames, email, password, rol_id) VALUES 
('Carlos', 'Ramírez Torres', 'admin@hotel.com', '1234', 1),
('María', 'González López', 'recep1@hotel.com', '1234', 2),
('Pedro', 'Sánchez Ruiz', 'recep2@hotel.com', '1234', 2),
('Ana', 'Torres Vega', 'servicios@hotel.com', '1234', 2);

-- CLIENTES (se registran desde la web)
INSERT INTO guests (names, surnames, dni, email, password, phone) VALUES 
('Juan', 'Pérez García', '12345678', 'juan@gmail.com', '1234', '987654321'),
('María', 'López Torres', '23456789', 'maria@gmail.com', '1234', '976543210'),
('Carlos', 'Rodríguez Silva', '34567890', 'carlos@gmail.com', '1234', '965432109'),
('Ana', 'Martínez Cruz', '45678901', 'ana@gmail.com', '1234', '954321098');

-- TIPOS DE HABITACIÓN
INSERT INTO room_type (name_type, description, capacity_people, base_price, high_season_price, amenities) VALUES 
('Habitación Simple', 'Habitación acogedora para una persona con todas las comodidades básicas', 1, 120.00, 160.00, 'WiFi, TV, Aire acondicionado, Baño privado'),
('Habitación Doble', 'Amplia habitación para dos personas con cama matrimonial', 2, 180.00, 240.00, 'WiFi, TV, Aire acondicionado, Baño privado, Minibar'),
('Suite Junior', 'Suite con sala de estar separada y vistas al jardín', 2, 280.00, 350.00, 'WiFi, TV Smart, Aire acondicionado, Jacuzzi, Minibar, Sala de estar'),
('Suite Doble', 'La más lujosa del hotel con todas las comodidades premium', 4, 450.00, 600.00, 'WiFi, 2 TVs Smart, Aire acondicionado, Jacuzzi, Bar privado, 2 Baños, Sala de estar, Comedor'),
('Habitación Familiar', 'Diseñada para familias con niños, espaciosa y segura', 4, 250.00, 320.00, 'WiFi, TV, Aire acondicionado, 2 Baños, Zona de juegos');

-- HABITACIONES FÍSICAS
INSERT INTO rooms (room_number, floor, state, type_id) VALUES 
-- Piso 1 - Simples
('101', 1, 'Disponible', 1),
('102', 1, 'Disponible', 1),
('103', 1, 'Mantenimiento', 1),
-- Piso 1 - Dobles
('104', 1, 'Disponible', 2),
('105', 1, 'Disponible', 2),
-- Piso 2 - Dobles
('201', 2, 'Disponible', 2),
('202', 2, 'Disponible', 2),
('203', 2, 'Disponible', 2),
-- Piso 2 - Suite Junior
('204', 2, 'Disponible', 3),
('205', 2, 'Disponible', 3),
-- Piso 3 - Suite Doble
('301', 3, 'Disponible', 4),
('302', 3, 'Disponible', 4),
-- Piso 3 - Familiar
('303', 3, 'Disponible', 5),
('304', 3, 'Disponible', 5);

-- CATÁLOGO DE SERVICIOS
INSERT INTO services_catalog (service_name, description, price, category) VALUES 
-- Bebidas
('Coca-Cola', 'Bebida gaseosa 500ml', 8.00, 'Bebidas'),
('Agua Mineral', 'Agua San Mateo 625ml', 5.00, 'Bebidas'),
('Cerveza Nacional', 'Cerveza Cristal o Pilsen 330ml', 12.00, 'Bebidas'),
('Cerveza Importada', 'Cerveza Heineken o Corona 330ml', 18.00, 'Bebidas'),
('Pisco Sour', 'Cóctel tradicional peruano', 25.00, 'Bebidas'),
('Vino Tinto Copa', 'Copa de vino tinto selección', 35.00, 'Bebidas'),
-- Comidas
('Desayuno Continental', 'Pan, jugos, café, frutas', 35.00, 'Comidas'),
('Desayuno Americano', 'Huevos, tocino, tostadas, jugos', 55.00, 'Comidas'),
('Almuerzo', 'Plato del día con bebida incluida', 65.00, 'Comidas'),
('Cena Romántica', 'Cena para dos con vino incluido', 180.00, 'Comidas'),
('Room Service', 'Servicio de comida a la habitación', 45.00, 'Comidas'),
('Snacks', 'Papitas, chocolates, maní', 15.00, 'Comidas'),
-- Spa y Bienestar
('Masaje Relajante 60min', 'Masaje corporal completo', 120.00, 'Spa'),
('Masaje en Pareja', 'Masaje para dos personas 60min', 220.00, 'Spa'),
('Tratamiento Facial', 'Limpieza facial profunda', 85.00, 'Spa'),
('Acceso Piscina', 'Acceso por día a piscina y jacuzzi', 30.00, 'Spa'),
-- Otros servicios
('Lavandería por pieza', 'Lavado y planchado por prenda', 10.00, 'Lavandería'),
('Lavandería Express', 'Servicio express en 3 horas', 25.00, 'Lavandería'),
('Transfer Aeropuerto', 'Traslado al aeropuerto', 80.00, 'Transporte'),
('Tour Ciudad', 'Paseo por la ciudad 4 horas', 150.00, 'Transporte'),
('Alquiler Bicicleta', 'Por día', 40.00, 'Entretenimiento'),
('Caja de Seguridad', 'Alquiler por noche', 15.00, 'Otros');

-- ============================================================
-- RESERVAS DE PRUEBA (diferentes estados)
-- ============================================================
INSERT INTO reservations (reservation_code, entry_date, departure_date, number_nights, 
    price_per_night, room_subtotal, total_consumption, total_pay, 
    state, guest_id, room_id) VALUES 
-- Reserva Pendiente (cliente 1, Suite Doble)
('RES-2026-0001', '2026-02-17', '2026-02-20', 3, 
    450.00, 1350.00, 0.00, 1350.00, 
    'Pendiente', 1, 4),
-- Reserva en Check-in (cliente 2, Habitación Doble)
('RES-2026-0002', '2026-02-14', '2026-02-16', 2, 
    180.00, 360.00, 33.00, 393.00, 
    'Check-in', 2, 5),
-- Reserva Completada/Check-out (cliente 3)
('RES-2026-0003', '2026-02-10', '2026-02-12', 2, 
    280.00, 560.00, 120.00, 680.00, 
    'Check-out', 3, 7),
-- Reserva Pendiente (cliente 4, Habitación Simple)
('RES-2026-0004', '2026-02-18', '2026-02-19', 1, 
    120.00, 120.00, 0.00, 120.00, 
    'Pendiente', 4, 2);

-- Actualizar estados de habitaciones según reservas
UPDATE rooms SET state = 'Reservada' WHERE room_id = 4;
UPDATE rooms SET state = 'Ocupada' WHERE room_id = 5;
-- CONSUMOS DE PRUEBA (reserva 2 en check-in)
-- ============================================================
INSERT INTO consumption (amount, unit_price, subtotal, observation, reservation_id, service_id, user_registration_id) VALUES 
(2, 8.00, 16.00, 'Pedido desde habitación', 1, 1, 2),
(1, 12.00, 12.00, 'Bar del hotel', 2, 3, 2),
(1, 5.00, 5.00, 'Minibar', 3, 2, 3);

-- ============================================================
-- PAGO DE PRUEBA (reserva 3 completada)
-- ============================================================
INSERT INTO payments (total_amount, payment_method, receipt_number, reservation_id, user_charge_id) VALUES 
(680.00, 'Tarjeta', 'COMP-2026-0001', 3, 2);

-- Actualizar checkout en reserva 3
UPDATE reservations SET 
    chekin_date = '2026-02-10 14:00:00',
    checkout_date = '2026-02-12 11:30:00',
    user_id_checkin = 2,
    user_id_checkout = 2
WHERE reservation_id = 3;