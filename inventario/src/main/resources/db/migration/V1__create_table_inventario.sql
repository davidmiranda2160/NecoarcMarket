CREATE TABLE inventario (
    id                   BIGINT   NOT NULL AUTO_INCREMENT PRIMARY KEY,
    producto_id          BIGINT   NOT NULL UNIQUE,
    cantidad             INT      NOT NULL DEFAULT 0,
    ultima_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);