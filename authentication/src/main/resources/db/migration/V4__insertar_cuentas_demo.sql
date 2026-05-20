UPDATE credencial
SET password_hash = '$2a$10$B.C3NdqQxeeojvinFuOo8OQVkExXebf7qRLggXDKKejgf6Tsefer6',
    activo = TRUE,
    fecha_actualizacion = NOW()
WHERE correo = 'admin@tiendajuegos.cl';

INSERT IGNORE INTO credencial
(id, usuario_id, correo, password_hash, activo, fecha_creacion, fecha_actualizacion)
VALUES
    (2, 2, 'jesus@tiendajuegos.cl', '$2a$10$s.DioALweTWNSBQWEwvUA.vllh.MgPUWuz3xaq5TtbX83guOTYg.a', TRUE, NOW(), NOW()),
    (3, 3, 'camila@tiendajuegos.cl', '$2a$10$s.DioALweTWNSBQWEwvUA.vllh.MgPUWuz3xaq5TtbX83guOTYg.a', TRUE, NOW(), NOW()),
    (4, 4, 'matias@tiendajuegos.cl', '$2a$10$s.DioALweTWNSBQWEwvUA.vllh.MgPUWuz3xaq5TtbX83guOTYg.a', TRUE, NOW(), NOW()),
    (5, 5, 'sofia@tiendajuegos.cl', '$2a$10$fwrgNil1iE0ZgyGD3pGXW.Ga6UOsW0eQJ7f6ZpdBP2i4fZ69oRvJe', TRUE, NOW(), NOW()),
    (6, 6, 'diego@tiendajuegos.cl', '$2a$10$YnsgwNwEEoLz0rUBn1PGzetxbl6bmtVTCqK.4SXDhbqiuOnETK8BS', TRUE, NOW(), NOW()),
    (7, 7, 'valentina@tiendajuegos.cl', '$2a$10$TKOVuCKqzwclvvq3XWPzOuf7hMl6cZ6vHNljIpWeDzQnkLD9GtTCy', TRUE, NOW(), NOW()),
    (8, 8, 'benjamin@tiendajuegos.cl', '$2a$10$6baj6UfhqIHf1nPzzOHttOGCcczE0278uEvMHEDPyBb9d5TxZDnB.', TRUE, NOW(), NOW());
