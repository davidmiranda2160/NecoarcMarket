NSERT INTO envios (orden_id, usuario_id, direccion_destino, codigo_seguimiento, estado_envio, empresa_transporte, fecha_estimada_entrega, fecha_creacion) 
VALUES 
(1, 1, 'Av. Vitacura 1234, Santiago', 'TRACK-NECO-001', 'PREPARANDO', 'Starken', '2026-05-20', '2026-05-17 14:30:00'),
(2, 2, 'Calle Los Olmos 432, San Bernardo', 'TRACK-NECO-002', 'EN_TRANSITO', 'Chilexpress', '2026-05-22', '2026-05-19 09:15:00'),
(3, 3, 'Pajaritos 2211, Maipú', 'TRACK-NECO-003', 'DESPACHADO', 'Starken', '2026-05-23', '2026-05-20 11:00:00'), -- Agregado para calzar con tu compañero
(4, 4, 'Pasaje El Sol 98, Maipú', 'TRACK-NECO-004', 'ENTREGADO', 'Blue Express', '2026-05-10', '2026-05-07 18:00:00');