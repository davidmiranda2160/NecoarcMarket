CREATE TABLE usuario (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    appaterno VARCHAR(150) NOT NULL,
    apmaterno VARCHAR(150),
    correo VARCHAR(255) NOT NULL UNIQUE,
    direccion VARCHAR(200) NOT NULL,
    telefono VARCHAR(50) NOT NULL,
    tipo_usuario VARCHAR(30) NOT NULL
);