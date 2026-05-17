INSERT INTO pagos (id_orden, metodo_pago, monto_a_pagar, monto_pagado, fecha_transaccion, estado_pago)
VALUES 
(1, 'Credito', 45000.00, 45000.00, '2026-05-15 14:30:00', 'Realizado'),
(2, 'Transferencia', 12500.00, 0.00, '2026-05-16 09:15:00', 'Pendiente'),
(3, 'Debito', 8990.00, 0.00, '2026-05-16 10:00:00', 'Rechazado');
