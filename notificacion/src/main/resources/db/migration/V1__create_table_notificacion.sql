CREATE TABLE notificacion (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id  BIGINT NOT NULL,
    mensaje     VARCHAR(255) NOT NULL,
    tipo        VARCHAR(50),
    fecha_envio DATETIME DEFAULT CURRENT_TIMESTAMP
);