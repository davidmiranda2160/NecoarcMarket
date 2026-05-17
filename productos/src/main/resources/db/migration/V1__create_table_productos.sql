CREATE TABLE productos (
    id           BIGINT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombrep      VARCHAR(150)    NOT NULL UNIQUE,
    descripcion  TEXT,
    precio       DECIMAL(10, 2)  NOT NULL,
    categoria    VARCHAR(255)    NULL,
    activo       BOOLEAN         NOT NULL DEFAULT TRUE,
    vendedor_id  BIGINT          NOT NULL
);