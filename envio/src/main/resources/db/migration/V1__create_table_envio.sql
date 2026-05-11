CREATE TABLE Envios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    orden_id BIGINT NOT NULL,
    direccion_destino VARCHAR(255) NOT NULL,
    numero_seguimiento VARCHAR(100) NOT NULL,
    estado_envio VARCHAR(50) NOT NULL,
    empresa_transporte VARCHAR(100) NOT NULL,
    fecha_estimada_entrega DATE NOT NULL
);