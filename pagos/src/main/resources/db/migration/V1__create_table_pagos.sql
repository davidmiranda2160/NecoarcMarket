CREATE TABLE pagos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_orden BIGINT NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL,
    monto_a_pagar DECIMAL(19, 2) NOT NULL,
    monto_pagado DECIMAL(19, 2) NOT NULL,
    fecha_transaccion DATETIME NOT NULL,
    estado_pago VARCHAR(30) NOT NULL
);