CREATE TABLE carrito (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad INT NOT NULL,
    monto_total DECIMAL(19, 2) NOT NULL
);