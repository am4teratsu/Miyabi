-- ============================================================
-- SCRIPT DE CREACIÓN COMPLETA - DB_Miyabi
-- ============================================================

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
    mobile_phone VARCHAR(15) COMMENT 'Teléfono móvil adicional',
    address VARCHAR(200),
    country VARCHAR(100) NOT NULL DEFAULT '' COMMENT 'País de procedencia',
    city VARCHAR(100) NOT NULL DEFAULT '' COMMENT 'Ciudad de procedencia',
    postal_code VARCHAR(20) COMMENT 'Código postal',
    registration_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    state TINYINT DEFAULT 1
);

-- TABLA 4: Tipo de habitacion
CREATE TABLE room_type (
    type_id INT AUTO_INCREMENT PRIMARY KEY,
    name_type VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    short_description VARCHAR(255) COMMENT 'Resumen breve en cursiva, ej: Dormitorio + terraza...',
    capacity_people INT NOT NULL DEFAULT 2,
    base_price DECIMAL(10,2) NOT NULL,
    high_season_price DECIMAL(10,2),
    image_url VARCHAR(500),
    floor_plan_url VARCHAR(500) COMMENT 'URL del croquis/plano arquitectónico',
    room_size VARCHAR(50) COMMENT 'Tamaño de la habitación, ej: 25 m²',
    location_info VARCHAR(150) COMMENT 'Ubicación específica, ej: Planta 2, vista al jardín',
    bed_type VARCHAR(150) COMMENT 'Detalle de camas, ej: 1 cama king size + sofá cama',
    amenities JSON COMMENT 'Lista de amenidades: ["WiFi","TV","Jacuzzi","Hamaca disponible"]'
);

-- TABLA 4.1: Carrusel de imágenes por tipo de habitación
CREATE TABLE room_images (
    image_id INT AUTO_INCREMENT PRIMARY KEY,
    image_url VARCHAR(500) NOT NULL COMMENT 'URL de la fotografía',
    alt_text VARCHAR(150) COMMENT 'Texto alternativo para accesibilidad',
    display_order INT DEFAULT 1 COMMENT 'Orden en el carrusel',
    is_main TINYINT DEFAULT 0 COMMENT '1=Imagen principal del carrusel',
    type_id INT NOT NULL,
    FOREIGN KEY (type_id) REFERENCES room_type(type_id) ON DELETE CASCADE
);

-- TABLA 5: Habitaciones
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
    state VARCHAR(20) NOT NULL DEFAULT 'Pending'
        COMMENT 'Pending, Confirmed, Check-in, Check-out, Cancelled',
    observations TEXT,
    num_adults INT NOT NULL DEFAULT 1 COMMENT 'Número de adultos en la reserva',
    num_children INT NOT NULL DEFAULT 0 COMMENT 'Número de niños en la reserva',
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
    FOREIGN KEY (user_id_checkout) REFERENCES users(user_id),
    CONSTRAINT chk_max_guests CHECK (num_adults + num_children <= 6),
    CONSTRAINT chk_min_adults CHECK (num_adults >= 1)
);

-- TABLA 7: Catalogo de servicios
CREATE TABLE services_catalog (
    service_id INT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(50) COMMENT 'Bebidas, Comidas, Spa, Lavandería, etc.',
    season ENUM('Autumn', 'Winter', 'Summer', 'Spring', 'All year') NOT NULL DEFAULT 'All year',
    available TINYINT DEFAULT 1 COMMENT '1=Active, 0=Inactive'
);

-- TABLA 8: Consumos
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

-- ============================================================
-- INSERCIONES DE DATOS
-- ============================================================

-- ROLES
INSERT INTO roles (name_rol, description) VALUES 
('Administrator', 'Acceso total al sistema, gestión de habitaciones y usuarios'),
('Receptionist', 'Gestión de reservas, check-in, check-out y servicios'),
('Client', 'Acceso web para ver y crear reservas');

-- USUARIOS
INSERT INTO users (names, surnames, email, password, rol_id) VALUES
-- ADMINS
('Rodrigo', 'Dalmagro López', 'onur@hotel.com', '1234', 1),
('Daniel', 'Montaner Miller', 'frod@hotel.com', '1234', 1),
-- RECEPCIONISTAS
('Christine', 'Chi Miller', 'potter@hotel.com', '1234', 2),
('Malkolm', 'Rench Lindberg', 'bonkar@hotel.com', '1234', 2),
('Milan', 'de Meij Visser', 'milan@hotel.com', '1234', 2),
('Eu-teum', 'Yoon Park', 'autumn@hotel.com', '1234', 2),
('Mert', 'Çelebi Kaya', 'key@hotel.com', '1234', 2),
('Ivo', 'Albino Silva', 'lohan@hotel.com', '1234', 2);

-- CLIENTES 
INSERT INTO guests (names, surnames, dni, email, password, phone) VALUES 
('Francisco', 'Aravena Toledo', '48291034', 'kingg@gmail.com', '1234', '090-4456-7781'),
('Rodrigo', 'Lombardi Da Silva', '91203485', 'spike@gmail.com', '1234', '090-2231-9940'),
('Guilherme', 'Oliviera Santos', '55672109', 'blowz@gmail.com', '1234', '090-8872-3312'),
('Bruno', 'Rodríguez Martínez', '10393847', 'neon@gmail.com', '1234', '090-1145-6678'),
('Eduardo', 'Nagahama Sato', '33495812', 'sato@gmail.com', '1234', '090-5561-2234'),
('Josh', 'Brunelli Smith', '77210493', 'pxs@gmail.com', '1234', '090-9903-4451'),
('Zachary', 'Patrone Miller', '84291035', 'zekken@gmail.com', '1234', '090-4412-8876'),
('Ian', 'Botsch Müller', '55102938', 'tex@gmail.com', '1234', '090-3341-9902'),
('Roberto', 'Rivas Sepúlveda', '12903847', 'mazino@gmail.com', '1234', '090-5567-2210'),
('Andrew', 'Maust Wilson', '77304912', 'verno@gmail.com', '1234', '090-8812-3345'),
('Erick', 'Santos Da Costa', '66291043', 'aspas@gmail.com', '1234', '090-1123-5567'),
('Ethan', 'Arnold Reed', '59302148', 'ethan@gmail.com', '1234', '090-4451-0029'),
('Georgio', 'Sanassy Fletcher', '22874103', 'keiko@gmail.com', '1234', '090-3382-1145'),
('Brock', 'Somerhalder Vance', '90123476', 'brawk@gmail.com', '1234', '090-5561-8832'),
('Adam', 'Pampuch Bélanger', '44102935', 'mada@gmail.com', '1234', '090-2210-9947'),
('Logan', 'Jenkins Ford', '77304512', 'skuba@gmail.com', '1234', '090-8843-2210'),
('Corbin', 'Lee Thompson', '33910284', 'com@gmail.com', '1234', '090-4412-9903'),
('Phat', 'Le Washington', '77203941', 'supamen@gmail.com', '1234', '090-2215-8847'),
('Bao', 'Nguyen Tran', '55104829', 'bao@gmail.com', '1234', '090-3361-2240'),
('Anthony', 'Nguyen Pham', '10293847', 'okeanos@gmail.com', '1234', '090-8842-3315'),
('Douglas', 'Silva Ferreira', '66291034', 'dgzin@gmail.com', '1234', '090-1123-5567'),
('Jake', 'Howlett Smith', '44291083', 'boaster@gmail.com', '1234', '090-4412-5590'),
('Austin', 'Roberts Jones', '16029384', 'crashies@gmail.com', '1234', '090-2210-9948'),
('Kajetan', 'Haremski Nowak', '77384920', 'kaajak@gmail.com', '1234', '090-3345-6612'),
('Sylvain', 'Pattyn Lefebvre', '55291034', 'veqaj@gmail.com', '1234', '090-8812-3345'),
('Emir', 'Ali Beder Yilmaz', '99102834', 'alfajer@gmail.com', '1234', '090-5567-2210'),
('Senxu', 'Wang Li', '88291034', 'nobody@gmail.com', '1234', '090-4412-7789'),
('Yongkang', 'Zheng Chen', '11429384', 'zmjjkk@gmail.com', '1234', '090-2231-5560'),
('Shunzhi', 'Wan Zhang', '77484912', 'chichoo@gmail.com', '1234', '090-3345-8812'),
('Qingchuan', 'Wang Liu', '55691043', 'cb@gmail.com', '1234', '090-8872-4415'),
('Zhao', 'Zhang Yang', '90123847', 'smoggy@gmail.com', '1234', '090-5561-9923'),
('Juncheng', 'Zhang Wu', '33491028', 'zjc@gmail.com', '1234', '090-1145-6678'),
('Juntai', 'Zhang Huang', '66293841', 'jieni7@gmail.com', '1234', '090-9903-2210'),
('Ali', 'Sargin Demir', '44829103', 'crewen@gmail.com', '1234', '090-4412-5598'),
('Yusuf', 'Kanber Kaya', '11029384', 'lar0k@gmail.com', '1234', '090-2210-6674'),
('Utku', 'Kart Sahin', '77384912', 'loita@gmail.com', '1234', '090-3345-9902'),
('Eren', 'Erzan Yıldız', '55291043', 'rose@gmail.com', '1234', '090-8812-4431'),
('Umut', 'Pekdoğan Çelik', '90123877', 'loversrock@gmail.com', '1234', '090-5567-1120'),
('Ayaz', 'Akhmetshin Ivanov', '33829104', 'nats@gmail.com', '1234', '090-4412-8856'),
('Semyon', 'Borchev Sokolov', '11029347', 'purp0@gmail.com', '1234', '090-2210-7741'),
('Wayne', 'Chang Tan', '77684912', 'wayne@gmail.com', '1234', '090-3345-9920'),
('Dominykas', 'Lukaševičius Kazlauskas', '54291043', 'miniboo@gmail.com', '1234', '090-8812-3347'),
('Kamil', 'Frąckowiak Wiśniewski', '92123847', 'kamo@gmail.com', '1234', '090-5567-1123'),
('Nico', 'Harms Schmidt', '22591038', 'harmii@gmail.com', '1234', '090-6641-0092');

INSERT INTO room_type (name_type, description, short_description, capacity_people, base_price, high_season_price, image_url, floor_plan_url, room_size, location_info, bed_type, amenities) VALUES 
('Habitación Japonesa Premier con Vistas al Jardín y Tatami', 
	'Una estética japonesa nueva, ligera y contemporánea impregna estas habitaciones, creadas mediante la cuidadosa disposición y superposición de materiales naturales tradicionales que "respiran" y envejecen con el tiempo, como el tatami, el papel japonés, el bambú y la tierra de diatomeas. Cada una de estas cuatro habitaciones cuenta con un baño privado de aguas termales al aire libre, una veranda de bambú y una terraza de madera, todos con vistas al Jardín del Bosque.', 
    'Habitación japonesa de tatami con sofá + terraza de madera + veranda de bambú + baño privado de aguas termales al aire libre',
    3, 110000, 180000, 
    'https://be.synxis.com/shs-ngbe-image-resizer/images/hotel/23382/images/medium/room/japanese_premier_0002.jpg',
    'https://mukayu.com/wp-content/themes/corporate/img/rooms/wa_drawing_en.svg',
    '70 ㎡', 'Situada en el primer piso o planta jardín, frente al Jardín del Bosque, con terraza de madera', NULL,
    '["Extras: Hamaca disponible bajo petición, sujeta a disponibilidad", "Mobiliario: Sofá incluido", "Descanso: Los huéspedes duermen en colchones futón tradicionales japoneses sobre el suelo de tatami"]' 
),
('Habitación Western Premier con Vistas al Jardín', 
	'Puro lujo occidental moderno con vistas al Jardín del Bosque desde la planta superior. Cada una de estas dos habitaciones cuenta con una terraza de madera muy amplia con tragaluz, un sofá confortable para relajarse, un escritorio y un baño privado de aguas termales al aire libre, creando una experiencia de refugio privado.', 
    'Dormitorio + terraza de madera + sofá + escritorio + baño privado de aguas termales al aire libre',
    2, 85000, 110000, 
    'https://be.synxis.com/shs-ngbe-image-resizer/images/hotel/23382/images/medium/room/juniorsuitezen0003.jpg',
    'https://mukayu.com/wp-content/themes/corporate/img/rooms/you_drawing_en.svg',
    '60 ㎡', 'Situada en la planta superior, frente al Jardín del Bosque, con una gran terraza de madera', NULL,	
    '["Extras: Hamaca disponible bajo petición, sujeta a disponibilidad", "Camas: 2 camas de tamaño completo (120 X 195 cm) que pueden combinarse", "Accesibilidad: Acceso mediante escalera (18 peldaños). No es apta para personas mayores o con movilidad reducida"]' 
),
('Junior Suite Estilo Zen', 
	'Creadas con materiales tradicionales japoneses, estas suites combinan el encanto y el confort de los estilos japonés y occidental. Ubicadas en la primera planta, muy cerca del Jardín del Bosque, cada una de estas dos junior suites cuenta con una terraza de madera, una veranda de bambú, una habitación de tatami japonés con sofá, un dormitorio independiente y un baño privado de aguas termales al aire libre equipado con jacuzzi.', 
    'Habitación de tatami japonés con sofá + dormitorio + terraza de madera + veranda de bambú + baño privado de aguas termales al aire libre',
    3, 95000, 135000, 
    'https://be.synxis.com/shs-ngbe-image-resizer/images/hotel/23382/images/medium/room/westerpremier0002.jpg',
    'https://mukayu.com/wp-content/themes/corporate/img/rooms/you_drawing_en.svg',
    '60 ㎡', 'Situada en la planta superior, frente al Jardín del Bosque, con una gran terraza de madera', '2 camas (120×195 cm / 47×76 pulgadas cada una)',
    '["Camas: 2 camas de tamaño completo (120 X 195 cm) que pueden combinarse", "Equipamiento: Sofá y jacuzzi privado"]' 
),
('Suite Ejecutiva Estilo Zen', 
	'Con una veranda de bambú, habitación de tatami japonés con vestidor, dormitorio y baño privado de aguas termales al aire libre, estas seis suites muy espaciosas (95–100 ㎡) ofrecen lo mejor de los estilos auténticos japonés y occidental con vistas al Jardín del Bosque. Desde los ventanales de las suites, todas situadas en las plantas superiores, los huéspedes pueden admirar árboles como pinos, cerezos de montaña (sakura), cerezos llorones, arces y camelias que extienden sus ramas libremente en el Jardín del Bosque', 
    'Habitación de tatami japonés + veranda de bambú + dormitorio + baño privado de aguas termales al aire libre',
    4, 125000, 180000, 
    'https://storage.googleapis.com/webimages-p1shrd/hotel/23382/images/room/web_ready_-_jpg-[executive_1]_mukayu_zen_executive_suite.jpg',
    'https://mukayu.com/wp-content/themes/corporate/img/rooms/wayou-ex_drawing_en.svg',
    '95–100 ㎡', 'Situada en las plantas 2ª, 3ª y 4ª, frente al Jardín del Bosque', '2 camas (120×195 cm / 47×76 pulgadas cada una)',
    '["Camas: 2 camas de tamaño completo ($120x195 cm / 47x76 pulgadas cada una) que pueden combinarse", "Equipamiento: Vestidor (walk-in closet)"]' 
),
('Suite Wakamurasaki', 
	'La suite más espaciosa (120 ㎡) con una vista inigualable de los cerezos en flor en primavera, verdes vibrantes en verano, hojas coloridas en otoño y la nieve cayendo en Winter. La suite Wakamurasaki cuenta con puertas correderas de suelo a techo que pueden abrirse ampliamente hacia el Jardín del Bosque. Durante la temporada de floración, los huéspedes pueden admirar el cerezo de montaña de más de 100 años justo frente a ellos, con los pétalos de sakura revoloteando hacia la veranda de bambú.', 
    'Habitación de tatami japonés + sala de estar + veranda de bambú + dormitorio + baño privado de aguas termales al aire libre',
    6, 150000, 220000, 
    'https://be.synxis.com/shs-ngbe-image-resizer/images/hotel/23382/images/medium/room/wakamurasaki_suite0001.jpg',
    'https://mukayu.com/wp-content/themes/corporate/img/rooms/wakamurasaki_drawing_en.svg',
    '120 ㎡', 'Situada en la 2ª planta, frente al Jardín del Bosque', '3 camas (120x195 cm / 47x76 pulgadas cada una)',
    '["Sala de estar: Incluye una mesa lacada de 3 metros de largo", "Camas: 3 camas de tamaño completo 2 camas (120x195 cm / 47x76 pulgadas cada una) que pueden combinarse", "Equipamiento: Vestidor (walk-in closet)"]' 
),
('Suite Terraza Byakuroku', 
	'Con un baño privado de aguas termales al aire libre y una amplia terraza de madera que se extiende hasta casi tocar el Jardín del Bosque, la suite Byakuroku es el lugar ideal para escapar del día a día mientras se purifica y rejuvenece el cuerpo y la mente. En esta espaciosa suite (110 m2), el estudio situado entre el baño y la terraza es el espacio perfecto para la imaginación creativa o el pensamiento meditativo que surge de una mente renovada.', 
    'Habitación de tatami japonés + estudio + veranda de bambú + terraza de madera + dormitorio + baño privado de aguas termales al aire libre',
    4, 125000, 180000, 
    'https://be.synxis.com/shs-ngbe-image-resizer/images/hotel/23382/images/medium/room/byakurokusuite1.jpg',
    'https://mukayu.com/wp-content/themes/corporate/img/rooms/byakuroku_drawing_en.svg',
    '110 ㎡', 'Situada en la 1ª planta, frente al Jardín del Bosque con una gran terraza de madera', '2 camas de tamaño completo (120x195 cm / 47x76 pulgadas cada una)',
    '["Estudio: Incluye un escritorio y sofá.", "Camas: 2 camas de tamaño completo (120x195 cm / 47x76 pulgadas cada una) que pueden combinarse.", "Equipamiento: Vestidor (walk-in closet)"]' 
);

-- IMAGENES DE LAS HABITACIONES
INSERT INTO room_images (image_url, alt_text, display_order, is_main, type_id) VALUES 
-- HABITACION 1
('https://mukayu.com/wp-content/themes/corporate/img/rooms/wa_img_01.jpg', 'Carrusel', 1, 1, 1),
('https://mukayu.com/wp-content/themes/corporate/img/rooms/wa_img_02.jpg', 'Carrusel', 2, 0, 1),
('https://mukayu.com/wp-content/themes/corporate/img/rooms/wa_img_03.jpg', 'Carrusel', 3, 0, 1),
('https://mukayu.com/wp-content/themes/corporate/img/rooms/wa_img_04.jpg', 'Carrusel', 4, 0, 1),
-- HABITACION 2
('https://mukayu.com/wp-content/themes/corporate/img/rooms/you_img_01.jpg', 'Carrusel', 1, 1, 2),
('https://mukayu.com/wp-content/themes/corporate/img/rooms/you_img_02.jpg', 'Carrusel', 2, 0, 2),
('https://mukayu.com/wp-content/themes/corporate/img/rooms/you_img_03.jpg', 'Carrusel', 3, 0, 2),
-- HABITACION 3
('https://mukayu.com/wp-content/themes/corporate/img/rooms/wayou-j_img_01.jpg', 'Carrusel', 1, 1, 3),
('https://mukayu.com/wp-content/themes/corporate/img/rooms/wayou-j_img_02.jpg', 'Carrusel', 2, 0, 3),
('https://mukayu.com/wp-content/themes/corporate/img/rooms/wayou-j_img_03.jpg', 'Carrusel', 3, 0, 3),
-- HABITACION 4
('https://mukayu.com/wp-content/themes/corporate/img/rooms/wayou_img_01.jpg', 'Carrusel', 1, 1, 4),
('https://mukayu.com/wp-content/themes/corporate/img/rooms/wayou_img_02.jpg', 'Carrusel', 2, 0, 4),
('https://mukayu.com/wp-content/themes/corporate/img/rooms/wayou_img_03.jpg', 'Carrusel', 3, 0, 4),
-- HABITACION 5
('https://mukayu.com/wp-content/themes/corporate/img/rooms/wakamurasaki_img_01.jpg', 'Carrusel', 1, 1, 5),
('https://mukayu.com/wp-content/themes/corporate/img/rooms/wakamurasaki_img_02.jpg', 'Carrusel', 2, 0, 5),
('https://mukayu.com/wp-content/themes/corporate/img/rooms/wakamurasaki_img_03.jpg', 'Carrusel', 3, 0, 5),
-- HABITACION 6
('https://mukayu.com/wp-content/themes/corporate/img/rooms/byakuroku_img_01.jpg', 'Carrusel', 1, 1, 6),
('https://mukayu.com/wp-content/themes/corporate/img/rooms/byakuroku_img_02.jpg', 'Carrusel', 2, 0, 6),
('https://mukayu.com/wp-content/themes/corporate/img/rooms/byakuroku_img_03.jpg', 'Carrusel', 3, 0, 6),
('https://mukayu.com/wp-content/themes/corporate/img/rooms/byakuroku_img_04.jpg', 'Carrusel', 4, 0, 6);


-- HABITACIONES FÍSICAS
INSERT INTO rooms (room_number, floor, state, type_id) VALUES 
('101', 1, 'Available', 1), -- Puerta 101 es la Habitación Japonesa Premier
('102', 1, 'Available', 2), -- Puerta 102 es la Habitación Western Premier
('201', 2, 'Available', 3), -- Puerta 201 es la Junior Suite Estilo Zen
('202', 2, 'Available', 4), -- Puerta 202 es la Suite Ejecutiva Estilo Zen
('301', 3, 'Available', 5), -- Puerta 301 es la Suite Wakamurasaki
('302', 3, 'Available', 6); -- Puerta 302 es la Suite Terraza Byakuroku

-- CATÁLOGO DE SERVICIOS
INSERT INTO services_catalog (service_name, description, price, category, season, available) VALUES
-- Winter
('Sashimi de Cangrejo de Nieve', 'Mejora de cena: incluye sashimi de cangrejo de nieve premium.', 9680.00, 'Comidas', 'Winter', 1),
('Pato Silvestre en olla caliente', 'Pato estilo Jibuni con gachas de arroz, mizuna y cebolla a la brasa.', 12100.00, 'Comidas', 'Winter', 1),
('Dumpling de ñame al vapor', 'Entrante delicado de ñame cocinado al vapor.', 1200.00, 'Comidas', 'Winter', 1),
('Cangrejo de nieve hembra', 'Servido con gelatina de ámbar especial.', 3500.00, 'Comidas', 'Winter', 1),
('Sopa de nabo con camarones', 'Sopa tradicional de invierno con nabos dulces y camarones.', 1500.00, 'Comidas', 'Winter', 1),
('Sashimi variado Toraebi', 'Camarones Toraebi, caracol de mar Baigai y jurel Buri.', 4200.00, 'Comidas', 'Winter', 1),
('Cangrejo de nieve a la brasa', 'Cangrejo de temporada cocinado lentamente a la brasa.', 5800.00, 'Comidas', 'Winter', 1),
('Arroz con Nodoguro', 'Arroz premium con lubina de garganta negra.', 3200.00, 'Comidas', 'Winter', 1),
('Sorbete de mandarina', 'Postre refrescante de cítricos de temporada.', 800.00, 'Comidas', 'Winter', 1),
('Menú Kaiseki de Cangrejo de Nieve', 'Mejora de cena premium: menú completo de cangrejo disponible del 7 de nov al 20 de marzo.', 26950.00, 'Comidas', 'Winter', 1),
('Sopa de nabo', 'Sopa tradicional de nabo, suave y reconfortante para el invierno.', 1450.00, 'Comidas', 'Winter', 1),
('Cangrejo hembra con gelatina de ámbar', 'Cangrejo de nieve hembra servido con una exclusiva gelatina de ámbar.', 3900.00, 'Comidas', 'Winter', 1),
('Vegetales locales con tofu y sésamo', 'Selección de vegetales de temporada con crema de tofu y pasta de sésamo.', 1200.00, 'Comidas', 'Winter', 1),
('Sashimi Variado Premium', 'Incluye cangrejo de nieve macho, jurel buri, camarón dulce amaebi y mero kue.', 4800.00, 'Comidas', 'Winter', 1),
('Cangrejo macho a la brasa', 'Cangrejo de nieve macho cocinado de forma artesanal a la brasa.', 6800.00, 'Comidas', 'Winter', 1),
('Dumpling de raíz de loto de Kaga', 'Especialidad regional preparada con raíz de loto de la zona de Kaga.', 1350.00, 'Comidas', 'Winter', 1),
('Arroz con cangrejo y karasumi', 'Arroz de temporada con carne de cangrejo y huevas de pescado curadas (karasumi).', 4200.00, 'Comidas', 'Winter', 1),
('Helado de almendras', 'Postre cremoso de almendras como cierre del menú Kaiseki.', 950.00, 'Comidas', 'Winter', 1),
('Menú completo de cangrejo de nieve', 'Mejora de cena premium: incluye variedades de cangrejo macho y hembra. Disponible del 7 de nov al 20 de marzo.', 47630.00, 'Comidas', 'Winter', 1),
('Sashimi de cangrejo de nieve macho', 'Cortes frescos de cangrejo de nieve macho premium servidos crudos.', 8500.00, 'Comidas', 'Winter', 1),
('Cangrejo de nieve macho hervido', 'Cangrejo de nieve macho entero cocinado al punto de sal tradicional.', 9200.00, 'Comidas', 'Winter', 1),
-- OTOÑO
('Siete Setas Regionales en olla caliente', 'Mejora de cena: incluye diversos sabores y texturas otoñales. Disponible del 1 de oct al 6 de nov.', 6050.00, 'Comidas', 'Autumn', 1),
('Setas Matsutake a la brasa', 'Mejora de cena: una pieza de aproximadamente 50g. Disponible del 1 de oct al 6 de nov.', 9680.00, 'Comidas', 'Autumn', 1),
('Sopa de pollo Yamanaka', 'Sopa preparada con hierbas medicinales tradicionales.', 1250.00, 'Comidas', 'Autumn', 1),
('Cangrejo velludo Kegani', 'Cangrejo premium del puerto de Hashitate.', 3950.00, 'Comidas', 'Autumn', 1),
('Matsutake en tetera dobin', 'Setas Matsutake al vapor servidas en la tradicional tetera de barro.', 2800.00, 'Comidas', 'Autumn', 1),
('Sashimi de Otoño', 'Variedad de camarón Amaebi, rodaballo Karei y calamar Akaika.', 3400.00, 'Comidas', 'Autumn', 1),
('Nodoguro con arroz tostado', 'Lubina de garganta negra a la brasa acompañada de arroz tostado.', 4800.00, 'Comidas', 'Autumn', 1),
('Vegetales de otoño a la brasa', 'Seta Shiitake, castaña, nueces de ginkgo y barracuda.', 1850.00, 'Comidas', 'Autumn', 1),
('Wagyu con salsa de higos', 'Ternera japonesa Wagyu a la brasa con una exclusiva salsa de higos.', 6200.00, 'Comidas', 'Autumn', 1),
('Arroz con Matsutake', 'Arroz de temporada cocinado con setas Matsutake frescas.', 2400.00, 'Comidas', 'Autumn', 1),
('Menú de setas Matsutake', 'Mejora de cena premium: menú completo centrado en la seta Matsutake. Disponible del 1 de oct al 6 de nov.', 18150.00, 'Comidas', 'Autumn', 1),
('Fideos soba con ñame rallado', 'Entrante de fideos tradicionales con ñame fresco rallado.', 1200.00, 'Comidas', 'Autumn', 1),
('Sashimi variado de otoño', 'Selección de mero Ako, caballa Sawara, almeja Shirogai y calamar Akaika.', 3800.00, 'Comidas', 'Autumn', 1),
('Estofado de cangrejo kegani y melón de cera', 'Plato regional que combina cangrejo velludo con la suavidad del melón de cera.', 4500.00, 'Comidas', 'Autumn', 1),
('Nodoguro y berenjenas a la brasa', 'Lubina de garganta negra acompañada de berenjenas asadas al estilo tradicional.', 4900.00, 'Comidas', 'Autumn', 1),
('Helado de leche de soja y pera de Kaga', 'Postre artesanal con peras locales de la región de Kaga.', 950.00, 'Comidas', 'Autumn', 1),
-- VERANO
('Vegetales de verano con pasta de nueces', 'Selección de vegetales frescos de estación acompañados de una delicada pasta de nueces.', 1200.00, 'Comidas', 'Summer', 1),
('Cangrejo velludo Kegani de Hashitate', 'Cangrejo Kegani de alta calidad proveniente directamente del puerto de Hashitate.', 4500.00, 'Comidas', 'Summer', 1),
('Sopa de almejas en pepino de Kaga', 'Sopa refrescante servida en el tradicional pepino grande de la región de Kaga.', 1650.00, 'Comidas', 'Summer', 1),
('Sashimi estival variado', 'Selección de temporada que incluye mero Jiara, pez limón Hiramasa y jurel Aji.', 3800.00, 'Comidas', 'Summer', 1),
('Pez dulce Ayu a la brasa', 'Especialidad de pez Ayu del desfiladero de Shogawa, cocinado lentamente a la brasa.', 3500.00, 'Comidas', 'Summer', 1),
('Vegetales de Kaga a fuego lento', 'Guiso regional con berenjena, calabaza castaña de piel roja y espinaca Kinjiso.', 2200.00, 'Comidas', 'Summer', 1),
('Arroz con abulón y caracoles Sazae', 'Plato premium de arroz cocinado con abulón Awabi y caracoles de mar locales.', 4800.00, 'Comidas', 'Summer', 1),
('Helado de almendras', 'Postre cremoso artesanal para cerrar el menú de verano.', 900.00, 'Comidas', 'Summer', 1),
('Sashimi de Abulón (Mejora)', 'Mejora de cena con abulón fresco (150-250g). El precio final depende del tamaño de la pieza.', 7500.00, 'Comidas', 'Summer', 1),
('Filete de Abulón (Mejora)', 'Abulón Awabi preparado al estilo filete. El precio final depende del tamaño de la pieza.', 8000.00, 'Comidas', 'Summer', 1),
('Abulón al vapor', 'Mejora de cena: incluye un abulón de 150g para 1-2 personas. Disponible del 28 de abril a mediados de septiembre.', 6050.00, 'Comidas', 'Summer', 1),
('Ostra de roca de temporada', 'Mejora de cena: ostra fresca disponible del 10 de junio al 9 de agosto. Requiere reserva anticipada.', 3000.00, 'Comidas', 'Summer', 1),
('Menú especial de abulón', 'Mejora premium: menú completo centrado en el abulón Awabi. Disponible del 20 de junio al 10 de septiembre (mín. 2 pers).', 30000.00, 'Comidas', 'Summer', 1),
('Abulón awabi cocido a fuego lento', 'Plato de abulón preparado con una técnica de cocción lenta y tradicional.', 4200.00, 'Comidas', 'Summer', 1),
('Sashimi de abulón awabi', 'Sashimi premium de abulón fresco con textura crujiente y sabor marino.', 5500.00, 'Comidas', 'Summer', 1),
('Abulón awabi a la brasa', 'Abulón asado a la parrilla para resaltar sus aromas ahumados.', 6000.00, 'Comidas', 'Summer', 1),
('Gachas de arroz con abulón awabi', 'Especialidad de arroz caldoso enriquecido con la esencia y trozos de abulón.', 3200.00, 'Comidas', 'Summer', 1),
('Menú de ostra de roca', 'Mejora de cena premium: menú completo centrado en la ostra Iwagaki. Disponible del 10 de junio al 9 de agosto.', 12100.00, 'Comidas', 'Summer', 1),
('Ostra de roca Iwagaki con jugo de limón', 'Ostra premium fresca servida con un toque cítrico de limón.', 2800.00, 'Comidas', 'Summer', 1),
('Sopa de dumpling de ostra de roca', 'Sopa tradicional con delicados dumplings elaborados con ostra Iwagaki.', 1950.00, 'Comidas', 'Summer', 1),
('Ostra de roca Iwagaki a la brasa', 'Ostra de temporada asada a la parrilla para resaltar su sabor natural.', 3200.00, 'Comidas', 'Summer', 1),
('Arroz con ostras de roca Iwagaki', 'Arroz de temporada cocinado a fuego lento con ostras de roca frescas.', 3500.00, 'Comidas', 'Summer', 1),
('Helado de shiso verde Oba', 'Postre refrescante elaborado con hojas de shiso verde Oba.', 950.00, 'Comidas', 'Summer', 1),
-- PRIMAVERA
('Menú degustación de brotes de bambú', 'Mejora de cena: menú completo centrado en brotes de bambú frescos. Disponible de finales de marzo a principios de mayo.', 6050.00, 'Comidas', 'Spring', 1),
('Abulón Awabi cocido a fuego lento', 'Abulón tierno preparado con técnicas de cocción lenta para resaltar su textura.', 4200.00, 'Comidas', 'Spring', 1),
('Vegetales de primavera con tofu y sésamo', 'Selección de vegetales frescos de estación con crema de tofu y pasta de sésamo.', 1250.00, 'Comidas', 'Spring', 1),
('Cangrejo velludo Kegani de Hashitate', 'Cangrejo Kegani premium capturado en el puerto de Hashitate.', 4500.00, 'Comidas', 'Spring', 1),
('Sashimi primaveral variado', 'Incluye camarones Shirotora, rodaballo Hirame y agujeta japonesa Sayori.', 3600.00, 'Comidas', 'Spring', 1),
('Brote de bambú a la brasa', 'Brotes de bambú de temporada asados a la brasa para un sabor ahumado.', 2200.00, 'Comidas', 'Spring', 1),
('Arroz con vieiras y erizo de mar', 'Plato de arroz premium cocinado con vieiras frescas y erizo de mar (Uni).', 3800.00, 'Comidas', 'Spring', 1),
('Olla caliente de almejas con vegetales', 'Caldo reconfortante de almejas frescas y vegetales verdes de primavera.', 3400.00, 'Comidas', 'Spring', 1),
('Helado de sal de Noto en oblea monaka', 'Postre tradicional en oblea crujiente con el toque único de la sal de Noto.', 950.00, 'Comidas', 'Spring', 1),
('Brotes de bambú en caldo', 'Plato de entrada con tiernos brotes de bambú de temporada en un caldo ligero.', 1250.00, 'Comidas', 'Spring', 1),
('Cangrejo velludo Kegani de Hashitate', 'Cangrejo premium capturado en el puerto de Hashitate, servido al estilo tradicional.', 4600.00, 'Comidas', 'Spring', 1),
('Sashimi: brotes de bambú y besugo Sakura', 'Variedad estival que incluye besugo Sakura, jurel Ajitataki y calamar Yariika.', 3900.00, 'Comidas', 'Spring', 1),
('Brotes de bambú con miso de hierba kinome', 'Brotes asados a la brasa y acompañados de una delicada pasta miso con aroma a kinome.', 1800.00, 'Comidas', 'Spring', 1),
('Nodoguro a la brasa', 'Lubina de garganta negra premium preparada a la brasa para resaltar su sabor natural.', 4950.00, 'Comidas', 'Spring', 1),
('Olla caliente de Wagyu y brotes de bambú', 'Especialidad Hot Pot que combina ternera japonesa Wagyu de alta calidad con vegetales de estación.', 12500.00, 'Comidas', 'Spring', 1),
('Arroz cocinado con brotes de bambú', 'Plato de arroz de temporada preparado a fuego lento con la esencia de los brotes de bambú.', 2600.00, 'Comidas', 'Spring', 1),
('Helado de sal de Noto en oblea monaka', 'Postre artesanal con sal marina de la región de Noto servido en una oblea crujiente.', 950.00, 'Comidas', 'Spring', 1),
-- TODO EL AÑO
('Cena de Sushi', 'Sustituye el plato de arroz del menú Kaiseki por seis piezas de sushi nigiri.', 3630.00, 'Comidas', 'All year', 1),
('Sushi Kaiseki de Kanazawa', 'Menú con mariscos frescos de Ishikawa que destaca el sushi y el sashimi (mín. 2 personas).', 9680.00, 'Comidas', 'All year', 1),
('Menú de carne Wagyu', 'Degustación de varios platos de carne que incluyen la prestigiosa ternera japonesa Wagyu.', 15000.00, 'Comidas', 'All year', 1),
('Menú vegetariano', 'Selección de platos preparados exclusivamente con vegetales locales japoneses de temporada.', 5500.00, 'Comidas', 'All year', 1),
-- SPA
('Yakushiyama Body (70 min)', 'Masaje de espalda, hombros y cabeza con bolas de hierbas medicinales y crema personalizada.', 30800.00, 'Spa', 'All year', 1),
('Yakushiyama Body (90 min)', 'Tratamiento corporal completo (full-body), hombros y cabeza con bolas de hierbas.', 34100.00, 'Spa', 'All year', 1),
('Yakushiyama Body (120 min)', 'Sesión extendida corporal completa, hombros y cabeza con terapia de hierbas medicinales.', 40700.00, 'Spa', 'All year', 1),
('Yakushiyama Facial (70 min)', 'Tratamiento facial, hombros y cabeza enfocado en la salud de la piel con hierbas locales.', 36300.00, 'Spa', 'All year', 1),
('Yakushiyama Facial (90 min)', 'Tratamiento de espalda, hombros, facial y cabeza con bolas de hierbas medicinales.', 39600.00, 'Spa', 'All year', 1),
('Combo Body & Facial (120 min)', 'Combinación de masaje de espalda, facial y cabeza con bolas de hierbas medicinales.', 44000.00, 'Spa', 'All year', 1),
('Combo Body & Facial (140 min)', 'Experiencia completa: cuerpo entero, facial y cabeza con terapia tradicional Yakushiyama.', 47300.00, 'Spa', 'All year', 1),
('Spring Sakura Treatment (70 min)', 'Masaje de cuerpo completo, hombros y cabeza. Oferta estacional de primavera.', 29700.00, 'Spa', 'Spring', 1),
('Spring Sakura Treatment (90 min)', 'Masaje de cuerpo completo, hombros y cabeza. Sesión extendida de temporada.', 33000.00, 'Spa', 'Spring', 1),
('Jet Lag Recovery (70 min)', 'Masaje de espalda, piernas, abdomen, hombros y cabeza para recuperación de viaje.', 29700.00, 'Spa', 'All year', 1),
('Jet Lag Recovery (90 min)', 'Masaje de cuerpo completo, abdomen, hombros y cabeza. Ideal tras vuelos largos.', 33000.00, 'Spa', 'All year', 1),
('Yakushiyama Pregnancy (70 min)', 'Tratamiento prenatal: espalda, hombros y cabeza con uso de bolas de hierbas medicinales.', 31900.00, 'Spa', 'All year', 1),
('Yakushiyama Pregnancy (90 min)', 'Tratamiento prenatal: espalda, facial y cabeza con uso de bolas de hierbas medicinales.', 40700.00, 'Spa', 'All year', 1),
('Muscle Therapy Body Treatment (70 min)', 'Masaje terapéutico muscular: espalda (solo), hombros y cabeza.', 28600.00, 'Spa', 'All year', 1),
('Muscle Therapy Body Treatment (90 min)', 'Masaje terapéutico muscular: cuerpo completo, hombros y cabeza.', 30800.00, 'Spa', 'All year', 1),
('Muscle Therapy Body Treatment (120 min)', 'Masaje terapéutico muscular: cuerpo completo, hombros y cabeza (sesión extendida).', 35200.00, 'Spa', 'All year', 1),
('Aromatherapy Body Treatment (50 min)', 'Tratamiento de aromaterapia: espalda (solo), hombros y cabeza.', 25300.00, 'Spa', 'All year', 1),
('Aromatherapy Body Treatment (70 min)', 'Tratamiento de aromaterapia: cuerpo completo y cabeza.', 27500.00, 'Spa', 'All year', 1),
('Japanese pressure point massage (50 min)', 'Masaje tradicional japonés de puntos de presión: cuerpo completo y cabeza.', 22000.00, 'Spa', 'All year', 1),
('Japanese pressure point massage (70 min)', 'Masaje tradicional japonés de puntos de presión: cuerpo completo y cabeza.', 25300.00, 'Spa', 'All year', 1),
('Treatment for abdomen (20 min)', 'Tratamiento que activa los intestinos enfocándose en puntos de acupuntura y circulación linfática.', 5500.00, 'Spa', 'All year', 1),
('Head Massage with IOU-stone eye pillow (20 min)', 'Masaje de cuero cabelludo con crema herbal medicinal y almohada térmica de piedra IOU.', 5500.00, 'Spa', 'All year', 1),
('Reflexology Foot Massage (20 min)', 'Terapia de reflexología que vitaliza el cuerpo mediante presión suave en las plantas de los pies.', 5500.00, 'Spa', 'All year', 1),
('Express Facial Treatment (30 min)', 'Tratamiento que incluye limpieza con "Yakushiyama Facial wash", masaje facial relajante y nutrición con loción y emulsión.', 13200.00, 'Spa', 'All year', 1),
-- PRODUCTOS
('Yakushiyama Shampoo (50ml)', 'Shampoo con 30 elementos de belleza que eliminan el sebo cuidando el cuero cabelludo. Aroma Refresh Blend.', 1430.00, 'Amenities', 'All year', 1),
('Yakushiyama Shampoo (400ml)', 'Shampoo premium con 30 elementos de belleza para un cabello sano y con volumen. Formato grande.', 5940.00, 'Amenities', 'All year', 1),
('Yakushiyama Conditioner (50ml)', 'Tratamiento capilar con 27 elementos de belleza vegetales y marinos. Aroma Relax blend.', 1540.00, 'Amenities', 'All year', 1),
('Yakushiyama Conditioner (400ml)', 'Acondicionador de esencia natural que repara el cabello desde el núcleo. Formato grande.', 6435.00, 'Amenities', 'All year', 1),
('Yakushiyama Body wash (50ml)', 'Gel de baño suave con 17 tipos de elementos de belleza naturales y colágeno marino. Aroma Irreplaceable blend.', 1430.00, 'Amenities', 'All year', 1),
('Yakushiyama Body wash (400ml)', 'Gel de baño premium que mejora la textura de la piel dañada por la sequedad. Formato grande.', 5940.00, 'Amenities', 'All year', 1),
('Yakushiyama Body lotion (50ml)', 'Loción corporal con 20 elementos hidratantes (escualano, vitamina A y ácido hialurónico). Aroma Irreplaceable.', 2420.00, 'Amenities', 'All year', 1),
('Yakushiyama Body lotion (250ml)', 'Tratamiento de hidratación profunda que mantiene los niveles de humedad de la piel. Formato grande.', 6600.00, 'Amenities', 'All year', 1),
('Yakushiyama Cleansing gel (50ml)', 'Gel limpiador que elimina maquillaje y exceso de sebo con colágeno marino. Aroma Relax blend.', 2640.00, 'Amenities', 'All year', 1),
('Yakushiyama Cleansing gel (250ml)', 'Gel limpiador premium que hidrata profundamente la capa córnea de la piel. Formato grande.', 7260.00, 'Amenities', 'All year', 1),
('Yakushiyama Facial wash (50ml)', 'Espuma limpiadora de aminoácidos con 22 elementos de belleza naturales. Aroma Relax blend.', 2970.00, 'Amenities', 'All year', 1),
('Yakushiyama Facial wash (250ml)', 'Limpiador facial que proporciona una textura suave y húmeda a la piel. Formato grande.', 8580.00, 'Amenities', 'All year', 1),
('Yakushiyama Lotion (50ml)', 'Esencia facial que combina loción y suero. Hidrata profundamente con 16 elementos de belleza y doble colágeno. Aroma Relax blend.', 4180.00, 'Amenities', 'All year', 1),
('Yakushiyama Lotion (250ml)', 'Loción de esencia premium para hidratación profunda y protección contra la sequedad. Formato grande.', 11550.00, 'Amenities', 'All year', 1),
('Yakushiyama Emulsion (50ml)', 'Emulsión de cuidado especial con escualano, colágeno marino nano y aminoácidos. Acabado lujoso. Aroma Relax blend.', 4290.00, 'Amenities', 'All year', 1),
('Yakushiyama Emulsion (250ml)', 'Emulsión de esencia para mejorar la textura de la piel y proporcionar un acabado suave y sedoso. Formato grande.', 13200.00, 'Amenities', 'All year', 1),
-- ACTIVIDADES
('Experiencia de elaboración de dulces Wagashi', 'Taller de 1.5h para aprender las bases de la creación de nerikiri con la experta Rika Yokoya usando herramientas tradicionales.', 11000.00, 'Activities', 'All year', 1),
('Degustación de té tostado Maruhachi', 'Experiencia de 1.5h para conocer el proceso de tostado del té Kenjo Kaga Boucha. Incluye degustación y dulces.', 8800.00, 'Activities', 'All year', 1),
('Ceremonia del té - Bienvenida de los propietarios', 'Una inmersión de 20 minutos en la cultura japonesa a través de una ceremonia ofrecida personalmente por los dueños del Ryokan.', 30000.00, 'Activities', 'All year', 1),
('Experiencia de elaboración de Chashaku (cuchara de té)', 'Taller de 80 minutos para tallar tu propia cuchara de bambú. Incluye el modelado artesanal y la firma con pincel y tinta.', 16500.00, 'Activities', 'All year', 1),
('Experiencia de vestimenta de Kimono', 'Taller de 2h en una tienda local para aprender el arte de vestir un kimono auténtico. Incluye elección de prenda y sesión de fotos en lugares icónicos de Yamashiro Onsen.', 18700.00, 'Activities', 'All year', 1),
('Experiencia Kinrande', 'Taller de 2.5 horas en el horno Kinzan Kutaniyaki para dominar el arte de aplicar pan de oro sobre porcelana Kutaniyaki. Aprende técnicas tradicionales para crear tu propia pieza única.', 22000.00, 'Activities', 'All year', 1),
('Experiencia de torneado de madera', 'Taller de 2 horas en el estudio del artista Terai Laku en las montañas de Yamanaka. Aprende técnicas de torneado de madera con más de 400 años de tradición y talla tu propia obra maestra.', 36000.00, 'Activities', 'All year', 1),
('Experiencia de fabricación de papel Kamisuki', 'Taller de 2h con Mika Horie sobre procesos del papel Washi. Crea tus propias postales con fibras de árboles Ganpi en un entorno natural de montaña.', 22000.00, 'Activities', 'All year', 1),
('Experiencia Wagatabon', 'Taller de 2.5h con el ebanista Rabea Gebler. Revive el arte del tallado de bandejas de castaño de la aldea de Wagatani y crea tu propia pieza artesanal.', 22000.00, 'Activities', 'All year', 1),
('Senderismo por la montaña Ozuchi', 'Aventura de 4h con Noboru Nimaida por la aldea oculta de Ozuchi. Visita casas de arcilla y manantiales. Disponible de abril a julio, septiembre y octubre.', 17600.00, 'Activities', 'All year', 1),
('Yoga de Reconexión', 'Sesión privada de 1h de yoga para alcanzar el estado de vacío "mu". Entorno con aromas naturales exclusivos. Requiere reserva con 3 días de antelación.', 16500.00, 'Activities', 'All year', 1),
('Experiencia de elaboración de bolas de hierbas medicinales de Hakusan', 'Taller de 1.5h para fabricar bolas de hierbas de Hakusan. Incluye selección de hierbas, vaporización termal, té y consejos de masaje para llevar a casa.', 13200.00, 'Activities', 'All year', 1),
('Apreciación y arreglo floral: Estética y Zen de Mukayu', 'Sesión de 1h de paseo por jardines y arreglo floral estilo ceremonia del té con flores de temporada. Tu creación decorará tu habitación tras el taller.', 13200.00, 'Activities', 'All year', 1);

-- RESERVAS
INSERT INTO reservations 
(reservation_code, entry_date, departure_date, number_nights, price_per_night, room_subtotal, total_pay, state, num_adults, guest_id, room_id, observations) 
VALUES
-- 15 de Marzo al 18 de Marzo (3 noches)
('RES-TEST-101', '2026-03-15', '2026-03-18', 3, 110000.00, 330000.00, 330000.00, 'Confirmed', 2, 1, 1, 'Bloqueo de prueba 101'),
('RES-TEST-102', '2026-03-15', '2026-03-18', 3, 85000.00, 255000.00, 255000.00, 'Confirmed', 2, 2, 2, 'Bloqueo de prueba 102'),
('RES-TEST-201', '2026-03-15', '2026-03-18', 3, 95000.00, 285000.00, 285000.00, 'Confirmed', 2, 3, 3, 'Bloqueo de prueba 201'),
('RES-TEST-202', '2026-03-15', '2026-03-18', 3, 125000.00, 375000.00, 375000.00, 'Confirmed', 2, 4, 4, 'Bloqueo de prueba 202'),
('RES-TEST-301', '2026-03-15', '2026-03-18', 3, 150000.00, 450000.00, 450000.00, 'Confirmed', 2, 5, 5, 'Bloqueo de prueba 301'),
('RES-TEST-302', '2026-03-15', '2026-03-18', 3, 125000.00, 375000.00, 375000.00, 'Confirmed', 2, 6, 6, 'Bloqueo de prueba 302'),
-- 22 de Marzo al 27 de Marzo (5 noches)
('RES-M22-101', '2026-03-22', '2026-03-27', 5, 110000.00, 550000.00, 550000.00, 'Confirmed', 2, 1, 1, 'Bloqueo visual Marzo'),
('RES-M22-102', '2026-03-22', '2026-03-27', 5, 85000.00, 425000.00, 425000.00, 'Confirmed', 2, 2, 2, 'Bloqueo visual Marzo'),
('RES-M22-201', '2026-03-22', '2026-03-27', 5, 95000.00, 475000.00, 475000.00, 'Confirmed', 2, 3, 3, 'Bloqueo visual Marzo'),
('RES-M22-202', '2026-03-22', '2026-03-27', 5, 125000.00, 625000.00, 625000.00, 'Confirmed', 2, 4, 4, 'Bloqueo visual Marzo'),
('RES-M22-301', '2026-03-22', '2026-03-27', 5, 150000.00, 750000.00, 750000.00, 'Confirmed', 2, 5, 5, 'Bloqueo visual Marzo'),
('RES-M22-302', '2026-03-22', '2026-03-27', 5, 125000.00, 625000.00, 625000.00, 'Confirmed', 2, 6, 6, 'Bloqueo visual Marzo'),
-- 10 de Abril al 14 de Abril (4 noches)
('RES-A10-101', '2026-04-10', '2026-04-14', 4, 110000.00, 440000.00, 440000.00, 'Confirmed', 2, 7, 1, 'Bloqueo visual Abril'),
('RES-A10-102', '2026-04-10', '2026-04-14', 4, 85000.00, 340000.00, 340000.00, 'Confirmed', 2, 8, 2, 'Bloqueo visual Abril'),
('RES-A10-201', '2026-04-10', '2026-04-14', 4, 95000.00, 380000.00, 380000.00, 'Confirmed', 2, 9, 3, 'Bloqueo visual Abril'),
('RES-A10-202', '2026-04-10', '2026-04-14', 4, 125000.00, 500000.00, 500000.00, 'Confirmed', 2, 10, 4, 'Bloqueo visual Abril'),
('RES-A10-301', '2026-04-10', '2026-04-14', 4, 150000.00, 600000.00, 600000.00, 'Confirmed', 2, 11, 5, 'Bloqueo visual Abril'),
('RES-A10-302', '2026-04-10', '2026-04-14', 4, 125000.00, 500000.00, 500000.00, 'Confirmed', 2, 12, 6, 'Bloqueo visual Abril'),
-- 5 de Marzo al 8 de Marzo (3 noches)
('RES-M05-101', '2026-03-05', '2026-03-08', 3, 110000.00, 330000.00, 330000.00, 'Confirmed', 2, 13, 1, 'Bloqueo inicio de Marzo'),
('RES-M05-102', '2026-03-05', '2026-03-08', 3, 85000.00, 255000.00, 255000.00, 'Confirmed', 2, 14, 2, 'Bloqueo inicio de Marzo'),
('RES-M05-201', '2026-03-05', '2026-03-08', 3, 95000.00, 285000.00, 285000.00, 'Confirmed', 2, 15, 3, 'Bloqueo inicio de Marzo'),
('RES-M05-202', '2026-03-05', '2026-03-08', 3, 125000.00, 375000.00, 375000.00, 'Confirmed', 2, 16, 4, 'Bloqueo inicio de Marzo'),
('RES-M05-301', '2026-03-05', '2026-03-08', 3, 150000.00, 450000.00, 450000.00, 'Confirmed', 2, 17, 5, 'Bloqueo inicio de Marzo'),
('RES-M05-302', '2026-03-05', '2026-03-08', 3, 125000.00, 375000.00, 375000.00, 'Confirmed', 2, 18, 6, 'Bloqueo inicio de Marzo'),
-- 1 de Abril al 5 de Abril (4 noches)
('RES-A01-101', '2026-04-01', '2026-04-05', 4, 110000.00, 440000.00, 440000.00, 'Confirmed', 2, 19, 1, 'Bloqueo inicio de Abril'),
('RES-A01-102', '2026-04-01', '2026-04-05', 4, 85000.00, 340000.00, 340000.00, 'Confirmed', 2, 20, 2, 'Bloqueo inicio de Abril'),
('RES-A01-201', '2026-04-01', '2026-04-05', 4, 95000.00, 380000.00, 380000.00, 'Confirmed', 2, 21, 3, 'Bloqueo inicio de Abril'),
('RES-A01-202', '2026-04-01', '2026-04-05', 4, 125000.00, 500000.00, 500000.00, 'Confirmed', 2, 22, 4, 'Bloqueo inicio de Abril'),
('RES-A01-301', '2026-04-01', '2026-04-05', 4, 150000.00, 600000.00, 600000.00, 'Confirmed', 2, 23, 5, 'Bloqueo inicio de Abril'),
('RES-A01-302', '2026-04-01', '2026-04-05', 4, 125000.00, 500000.00, 500000.00, 'Confirmed', 2, 24, 6, 'Bloqueo inicio de Abril'),
-- BLOQUE 5: 20 de Abril al 25 de Abril (5 noches)
('RES-A20-101', '2026-04-20', '2026-04-25', 5, 110000.00, 550000.00, 550000.00, 'Confirmed', 2, 25, 1, 'Bloqueo fin de Abril'),
('RES-A20-102', '2026-04-20', '2026-04-25', 5, 85000.00, 425000.00, 425000.00, 'Confirmed', 2, 26, 2, 'Bloqueo fin de Abril'),
('RES-A20-201', '2026-04-20', '2026-04-25', 5, 95000.00, 475000.00, 475000.00, 'Confirmed', 2, 27, 3, 'Bloqueo fin de Abril'),
('RES-A20-202', '2026-04-20', '2026-04-25', 5, 125000.00, 625000.00, 625000.00, 'Confirmed', 2, 28, 4, 'Bloqueo fin de Abril'),
('RES-A20-301', '2026-04-20', '2026-04-25', 5, 150000.00, 750000.00, 750000.00, 'Confirmed', 2, 29, 5, 'Bloqueo fin de Abril'),
('RES-A20-302', '2026-04-20', '2026-04-25', 5, 125000.00, 625000.00, 625000.00, 'Confirmed', 2, 30, 6, 'Bloqueo fin de Abril');

-- RESERVAS CON CHECKOUT Y CONSUMOS
-- 1. RESERVA 1 (Habitación 101, Enero 2026 - Con consumo alto)
INSERT INTO reservations 
(reservation_code, entry_date, departure_date, number_nights, price_per_night, room_subtotal, total_consumption, total_pay, state, num_adults, guest_id, room_id, observations) 
VALUES 
('RES-HIST-001', '2026-01-10', '2026-01-12', 2, 110000.00, 220000.00, 19360.00, 239360.00, 'Check-out', 2, 1, 1, 'Estancia finalizada con éxito. Consumo en restaurante.');

SET @res1 = LAST_INSERT_ID();

INSERT INTO consumption (amount, unit_price, subtotal, observation, reservation_id, service_id)
VALUES (2, 9680.00, 19360.00, 'Sashimi de Cangrejo de Nieve (x2)', @res1, 1);

INSERT INTO payments (total_amount, payment_method, payment_status, reservation_id, observation)
VALUES (239360.00, 'Tarjeta', 'Paid', @res1, 'Pago total completado en Check-out');


-- 2. RESERVA 2 (Habitación 102, Febrero 2026 - Con consumo medio)
INSERT INTO reservations 
(reservation_code, entry_date, departure_date, number_nights, price_per_night, room_subtotal, total_consumption, total_pay, state, num_adults, guest_id, room_id, observations) 
VALUES 
('RES-HIST-002', '2026-02-05', '2026-02-08', 3, 85000.00, 255000.00, 12100.00, 267100.00, 'Check-out', 2, 1, 2, 'Huésped habitual. Solicitó pato en olla caliente.');

SET @res2 = LAST_INSERT_ID();

INSERT INTO consumption (amount, unit_price, subtotal, observation, reservation_id, service_id)
VALUES (1, 12100.00, 12100.00, 'Pato Silvestre en olla caliente', @res2, 2);

INSERT INTO payments (total_amount, payment_method, payment_status, reservation_id, observation)
VALUES (267100.00, 'Efectivo', 'Paid', @res2, 'Pago en efectivo al retirarse');


-- 3. RESERVA 3 (Habitación 201, Mediados de Febrero 2026 - Consumo bajo)
INSERT INTO reservations 
(reservation_code, entry_date, departure_date, number_nights, price_per_night, room_subtotal, total_consumption, total_pay, state, num_adults, guest_id, room_id, observations) 
VALUES 
('RES-HIST-003', '2026-02-15', '2026-02-16', 1, 95000.00, 95000.00, 3600.00, 98600.00, 'Check-out', 2, 1, 3, 'Viaje rápido de negocios.');

SET @res3 = LAST_INSERT_ID();

INSERT INTO consumption (amount, unit_price, subtotal, observation, reservation_id, service_id)
VALUES (3, 1200.00, 3600.00, 'Dumpling de ñame al vapor (x3)', @res3, 3);

INSERT INTO payments (total_amount, payment_method, payment_status, reservation_id, observation)
VALUES (98600.00, 'Transferencia', 'Paid', @res3, 'Pago previo y consumos cobrados al final');
SELECT * FROM reservations;
SELECT * FROM guests;

-- BOLETA DE JASPER
SELECT c.amount, s.service_name, c.unit_price, c.subtotal 
FROM consumption c 
INNER JOIN services_catalog s ON c.service_id = s.service_id 
WHERE c.reservation_id = ?;

SELECT g.names, g.surnames, r.reservation_date, r.total_pay, 
       r.number_nights, r.price_per_night, r.room_subtotal, rt.name_type
FROM reservations r
INNER JOIN guests g ON r.guest_id = g.guest_id
INNER JOIN rooms rm ON r.room_id = rm.room_id
INNER JOIN room_type rt ON rm.type_id = rt.type_id
WHERE r.reservation_id = ?;