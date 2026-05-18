INSERT IGNORE INTO pago
(id, usuario_id, monto, metodo_pago, estado, codigo_transaccion, fecha_pago)
VALUES
    (1, 2, 87970, 'TARJETA', 'APROBADO', 'SEED-PAGO-0001', NOW());
