INSERT IGNORE INTO item_carrito
(id, usuario_id, videojuego_id, cantidad, precio_unitario, subtotal, fecha_agregado)
VALUES
    (1, 2, 1, 1, 37990, 37990, NOW()),
    (2, 2, 2, 2, 24990, 49980, NOW());
