CREATE TABLE ordenes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,        -- En Java es: usuarioId
    fecha_orden DATETIME NOT NULL,     -- En Java es: fechaOrden
    estado_orden VARCHAR(30) NOT NULL, -- En Java es: estadoOrden
    total DECIMAL(19, 2) NOT NULL      -- En Java es: total
);