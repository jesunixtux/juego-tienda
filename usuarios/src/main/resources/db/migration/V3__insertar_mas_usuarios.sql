INSERT IGNORE INTO usuario
(id, nombre, apellido, correo, telefono, direccion, rol, activo, fecha_registro)
VALUES
    (5, 'Sofia', 'Morales', 'sofia@tiendajuegos.cl', '+56955555555', 'Nunoa', 'CLIENTE', TRUE, NOW()),
    (6, 'Diego', 'Fernandez', 'diego@tiendajuegos.cl', '+56966666666', 'Macul', 'CLIENTE', TRUE, NOW()),
    (7, 'Valentina', 'Soto', 'valentina@tiendajuegos.cl', '+56977777777', 'Maipu', 'CLIENTE', TRUE, NOW()),
    (8, 'Benjamin', 'Herrera', 'benjamin@tiendajuegos.cl', '+56988888888', 'San Miguel', 'CLIENTE', TRUE, NOW());
