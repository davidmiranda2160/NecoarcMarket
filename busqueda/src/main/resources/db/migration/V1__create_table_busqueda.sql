CREATE TABLE busqueda (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_seguimiento VARCHAR(50) NOT NULL UNIQUE,
    envio_id BIGINT NOT NULL,          
    estado_envio VARCHAR(50) NOT NULL,
    detalle VARCHAR(255),
    fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP
);