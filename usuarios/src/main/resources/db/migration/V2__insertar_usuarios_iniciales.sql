INSERT IGNORE INTO usuario
(id, nombre, apellido, correo, telefono, direccion, rol, activo, fecha_registro)
VALUES
    (1, 'Admin', 'Tienda', 'admin@tiendajuegos.cl', '+56911111111', 'Santiago Centro', 'ADMIN', TRUE, NOW()),
    (2, 'Jesus', 'Emilio', 'jesus@tiendajuegos.cl', '+56922222222', 'Providencia', 'CLIENTE', TRUE, NOW()),
    (3, 'Camila', 'Torres', 'camila@tiendajuegos.cl', '+56933333333', 'Las Condes', 'CLIENTE', TRUE, NOW()),
    (4, 'Matias', 'Rojas', 'matias@tiendajuegos.cl', '+56944444444', 'La Florida', 'CLIENTE', TRUE, NOW());
