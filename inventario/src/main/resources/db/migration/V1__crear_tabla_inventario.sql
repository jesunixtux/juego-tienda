CREATE TABLE IF NOT EXISTS inventario (
    id BIGINT NOT NULL AUTO_INCREMENT,
    videojuego_id BIGINT NOT NULL,
    stock INT NOT NULL,
    stock_minimo INT NOT NULL,
    fecha_actualizacion DATETIME(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_inventario_videojuego_id (videojuego_id)
);
