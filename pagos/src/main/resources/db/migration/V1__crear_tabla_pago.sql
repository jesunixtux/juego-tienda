CREATE TABLE IF NOT EXISTS pago (
    id BIGINT NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    monto INT NOT NULL,
    metodo_pago VARCHAR(255) NOT NULL,
    estado VARCHAR(255) NOT NULL,
    codigo_transaccion VARCHAR(255) NOT NULL,
    fecha_pago DATETIME(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_pago_codigo_transaccion (codigo_transaccion)
);
