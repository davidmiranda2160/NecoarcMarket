CREATE TABLE resena (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id     BIGINT NOT NULL,
    usuario_id      BIGINT NOT NULL,
    calificacion    INT NOT NULL,
    comentario      VARCHAR(500),
    fecha_creacion  DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_calificacion CHECK (calificacion BETWEEN 1 AND 5)
);