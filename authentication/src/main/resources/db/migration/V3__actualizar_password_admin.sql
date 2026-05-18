UPDATE credencial
SET password_hash = '$2a$10$HDT.cqmtlBCdN2lMrrGNieIvf1RRAK3RkuZwDQNOjEZ0q52kG4p3m',
    fecha_actualizacion = NOW()
WHERE correo = 'admin@tiendajuegos.cl';
