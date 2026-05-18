CREATE TABLE IF NOT EXISTS resenas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_usuario BIGINT NOT NULL,
    nombre_juego VARCHAR(100) NOT NULL,
    comentario VARCHAR(1000) NOT NULL,
    puntuacion INT NOT NULL,
    fecha_resena DATE NOT NULL,
    PRIMARY KEY (id)
);
