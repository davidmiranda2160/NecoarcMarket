INSERT INTO envios (
orden_id, usuario_id,direccion_destino, numero_seguimiento, estado_envio, empresa_transporte, fecha_estimada_entrega, fecha_creacion) 
VALUES 
(1, 1, 'Av. Vitacura 1234, Santiago', 'TRK1715400000', 'PREPARANDO', 'Starken', '2026-05-20', '2026-05-17 14:30:00'),
(2, 2, 'Calle Los Olmos 432, San Bernardo', 'TRK1715400001', 'EN_TRANSITO', 'Chilexpress', '2026-05-22', '2026-05-19 09:15:00'),
(4, 4, 'Pasaje El Sol 98, Maipú', 'TRK1715400002', 'ENTREGADO', 'Blue Express', '2026-05-10', '2026-05-07 18:00:00');