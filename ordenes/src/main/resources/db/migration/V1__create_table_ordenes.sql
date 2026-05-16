CREATE TABLE ordenes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    id_envio BIGINT NOT NULL,
    fecha_creacion DATETIME NOT NULL,
    estado_orden VARCHAR(30) NOT NULL,
    monto_total DECIMAL(19, 2) NOT NULL,
    direccion_envio VARCHAR(255) NOT NULL,
    id_pago BIGINT NOT NULL
);