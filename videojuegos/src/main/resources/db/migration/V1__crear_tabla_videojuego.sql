CREATE TABLE IF NOT EXISTS video_juego (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    categoria VARCHAR(255) NOT NULL,
    precio INT NOT NULL,
    plataforma VARCHAR(255) NOT NULL,
    descripcion VARCHAR(1000),
    desarrollador VARCHAR(255),
    fecha_lanzamiento DATE,
    imagen_url VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (id)
);
