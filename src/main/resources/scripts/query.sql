-- ============================================================
-- SCRIPT DE CAMBIOS - DB_Miyabi
-- Fecha: 24/02/2026
-- ============================================================

USE DB_Miyabi;

-- ============================================================
-- CAMBIO 1: Agregar campos a tabla RESERVATIONS

-- ============================================================

ALTER TABLE reservations
    ADD COLUMN num_adults INT NOT NULL DEFAULT 1 
        COMMENT 'Número de adultos en la reserva' 
        AFTER observations,
    ADD COLUMN num_children INT NOT NULL DEFAULT 0 
        COMMENT 'Número de niños en la reserva' 
        AFTER num_adults;

-- Constraint: máximo 6 personas en total
ALTER TABLE reservations
    ADD CONSTRAINT chk_max_guests 
        CHECK (num_adults + num_children <= 6),
    ADD CONSTRAINT chk_min_adults 
        CHECK (num_adults >= 1);


-- ============================================================
-- CAMBIO 2: Agregar campos a tabla GUESTS
-- País, Ciudad, Código Postal y Teléfono móvil
-- ============================================================

ALTER TABLE guests
    ADD COLUMN country VARCHAR(100) NOT NULL DEFAULT '' 
        COMMENT 'País de procedencia' 
        AFTER address,
    ADD COLUMN city VARCHAR(100) NOT NULL DEFAULT '' 
        COMMENT 'Ciudad de procedencia' 
        AFTER country,
    ADD COLUMN postal_code VARCHAR(20) 
        COMMENT 'Código postal' 
        AFTER city,
    ADD COLUMN mobile_phone VARCHAR(15) 
        COMMENT 'Teléfono móvil adicional' 
        AFTER phone;


-- ============================================================
-- CAMBIO 3: Modificar tabla ROOM_TYPE
-- Ampliar campos existentes y agregar nuevos
-- ============================================================

-- 3.1 Ampliar name_type de VARCHAR(50) a VARCHAR(100)
ALTER TABLE room_type
    MODIFY COLUMN name_type VARCHAR(100) NOT NULL;

-- 3.2 Agregar nuevos campos
ALTER TABLE room_type
    ADD COLUMN short_description VARCHAR(255) 
        COMMENT 'Resumen breve en cursiva, ej: Dormitorio + terraza...' 
        AFTER description,
    ADD COLUMN floor_plan_url VARCHAR(500) 
        COMMENT 'URL del croquis/plano arquitectónico' 
        AFTER image_url,
    ADD COLUMN room_size VARCHAR(50) 
        COMMENT 'Tamaño de la habitación, ej: 25 m²' 
        AFTER floor_plan_url,
    ADD COLUMN location_info VARCHAR(150) 
        COMMENT 'Ubicación específica, ej: Planta 2, vista al jardín' 
        AFTER room_size,
    ADD COLUMN bed_type VARCHAR(150) 
        COMMENT 'Detalle de camas, ej: 1 cama king size + sofá cama' 
        AFTER location_info;

-- 3.3 Cambiar amenities a JSON para soportar lista variable
ALTER TABLE room_type
    MODIFY COLUMN amenities JSON 
        COMMENT 'Lista de amenidades: ["WiFi","TV","Jacuzzi","Hamaca disponible"]';


-- ============================================================
-- CAMBIO 4: Nueva tabla ROOM_IMAGES
-- Carrusel de imágenes por tipo de habitación
-- ============================================================

CREATE TABLE room_images (
    image_id INT AUTO_INCREMENT PRIMARY KEY,
    image_url VARCHAR(500) NOT NULL COMMENT 'URL de la fotografía',
    alt_text VARCHAR(150) COMMENT 'Texto alternativo para accesibilidad',
    display_order INT DEFAULT 1 COMMENT 'Orden en el carrusel',
    is_main TINYINT DEFAULT 0 COMMENT '1=Imagen principal del carrusel',
    type_id INT NOT NULL,
    FOREIGN KEY (type_id) REFERENCES room_type(type_id) ON DELETE CASCADE
);


-- ============================================================
-- ACTUALIZAR DATOS EXISTENTES (amenities a formato JSON)
-- ============================================================

UPDATE room_type SET amenities = '["WiFi","TV","Aire acondicionado","Baño privado"]' 
    WHERE type_id = 1;

UPDATE room_type SET amenities = '["WiFi","TV","Aire acondicionado","Baño privado","Minibar"]' 
    WHERE type_id = 2;

UPDATE room_type SET amenities = '["WiFi","TV Smart","Aire acondicionado","Jacuzzi","Minibar","Sala de estar"]' 
    WHERE type_id = 3;

UPDATE room_type SET amenities = '["WiFi","2 TVs Smart","Aire acondicionado","Jacuzzi","Bar privado","2 Baños","Sala de estar","Comedor"]' 
    WHERE type_id = 4;

UPDATE room_type SET amenities = '["WiFi","TV","Aire acondicionado","2 Baños","Zona de juegos"]' 
    WHERE type_id = 5;


-- ============================================================
-- VERIFICAR CAMBIOS
-- ============================================================

DESCRIBE reservations;
DESCRIBE guests;
DESCRIBE room_type;
DESCRIBE room_images;