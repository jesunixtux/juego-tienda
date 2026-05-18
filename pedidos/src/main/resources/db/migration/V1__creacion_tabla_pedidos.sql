CREATE TABLE IF NOT EXISTS pedidos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_usuario BIGINT NOT NULL,
    nombre_juego VARCHAR(100) NOT NULL,
    precio DOUBLE NOT NULL,
    fecha_pedido DATE NOT NULL,
    PRIMARY KEY (id)
);
