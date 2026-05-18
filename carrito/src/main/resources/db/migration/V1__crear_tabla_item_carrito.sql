CREATE TABLE IF NOT EXISTS item_carrito (
    id BIGINT NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    videojuego_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario INT NOT NULL,
    subtotal INT,
    fecha_agregado DATETIME(6),
    PRIMARY KEY (id)
);
