CREATE TABLE IF NOT EXISTS credencial (
    id BIGINT NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    correo VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion DATETIME(6),
    fecha_actualizacion DATETIME(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_credencial_usuario_id (usuario_id),
    UNIQUE KEY uk_credencial_correo (correo)
);
