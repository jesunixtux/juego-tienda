INSERT IGNORE INTO credencial
(id, usuario_id, correo, password_hash, activo, fecha_creacion, fecha_actualizacion)
VALUES
    (1, 1, 'admin@tiendajuegos.cl', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', TRUE, NOW(), NOW());
