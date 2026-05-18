INSERT IGNORE INTO inventario
(id, videojuego_id, stock, stock_minimo, fecha_actualizacion)
VALUES
    (1, 1, 15, 3, NOW()),
    (2, 2, 30, 5, NOW()),
    (3, 3, 8, 2, NOW()),
    (4, 4, 20, 4, NOW()),
    (5, 5, 12, 3, NOW());
