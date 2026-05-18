INSERT IGNORE INTO video_juego
(id, nombre, categoria, precio, plataforma, descripcion, desarrollador, fecha_lanzamiento, imagen_url, activo)
VALUES
    (1, 'Cyberpunk 2077', 'RPG', 37990, 'PC', 'RPG de mundo abierto ambientado en Night City.', 'CD Projekt Red', '2020-12-10', 'https://cdn.cloudflare.steamstatic.com/steam/apps/1091500/header.jpg', TRUE),
    (2, 'Minecraft', 'Sandbox', 24990, 'PC', 'Juego de construccion y supervivencia en mundo abierto.', 'Mojang Studios', '2011-11-18', 'https://cdn.cloudflare.steamstatic.com/steam/apps/1672970/header.jpg', TRUE),
    (3, 'God of War Ragnarok', 'Accion', 49990, 'PlayStation', 'Aventura de accion basada en mitologia nordica.', 'Santa Monica Studio', '2022-11-09', 'https://cdn.cloudflare.steamstatic.com/steam/apps/2322010/header.jpg', TRUE),
    (4, 'Stardew Valley', 'Simulacion', 9990, 'PC', 'Simulador de granja, exploracion y comunidad.', 'ConcernedApe', '2016-02-26', 'https://cdn.cloudflare.steamstatic.com/steam/apps/413150/header.jpg', TRUE),
    (5, 'The Legend of Zelda: Tears of the Kingdom', 'Aventura', 59990, 'Nintendo Switch', 'Aventura de exploracion en Hyrule.', 'Nintendo', '2023-05-12', NULL, TRUE);
