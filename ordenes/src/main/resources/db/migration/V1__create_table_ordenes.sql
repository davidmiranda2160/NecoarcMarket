CREATE TABLE ordenes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,        
    fecha_orden DATETIME NOT NULL,     
    estado_orden VARCHAR(30) NOT NULL, 
    total DECIMAL(19, 2) NOT NULL
);