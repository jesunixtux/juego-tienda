<?php
$dbHost = getenv('DB_HOST') ?: 'mysql';
$dbPort = getenv('DB_PORT') ?: '3306';
$dbUser = getenv('DB_USER') ?: 'root';
$dbPassword = getenv('DB_PASSWORD') ?: '';
$apiGatewayUrl = getenv('API_GATEWAY_URL') ?: 'http://localhost:8080';
$phpMyAdminUrl = getenv('PHPMYADMIN_URL') ?: 'http://localhost:8082';

$databaseStatus = 'Pendiente';
$databaseMessage = 'No se pudo revisar la conexion.';

try {
    $pdo = new PDO(
        "mysql:host={$dbHost};port={$dbPort};charset=utf8mb4",
        $dbUser,
        $dbPassword,
        [PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION]
    );

    $version = $pdo->query('SELECT VERSION()')->fetchColumn();
    $databaseStatus = 'OK';
    $databaseMessage = "Conexion correcta a MySQL {$version}";
} catch (Throwable $exception) {
    $databaseStatus = 'Error';
    $databaseMessage = $exception->getMessage();
}
?>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Tienda Videojuegos - Apache PHP</title>
    <style>
        :root {
            color-scheme: light dark;
            font-family: Arial, Helvetica, sans-serif;
        }

        body {
            margin: 0;
            min-height: 100vh;
            display: grid;
            place-items: center;
            background: #f4f6f8;
            color: #17202a;
        }

        main {
            width: min(720px, calc(100vw - 32px));
            padding: 28px;
            border: 1px solid #d8dee6;
            border-radius: 8px;
            background: #ffffff;
            box-shadow: 0 12px 35px rgba(23, 32, 42, 0.08);
        }

        h1 {
            margin: 0 0 12px;
            font-size: 28px;
        }

        p {
            line-height: 1.5;
        }

        dl {
            display: grid;
            grid-template-columns: max-content 1fr;
            gap: 10px 16px;
            margin: 24px 0;
        }

        dt {
            font-weight: 700;
        }

        dd {
            margin: 0;
            overflow-wrap: anywhere;
        }

        .status-ok {
            color: #147a3d;
            font-weight: 700;
        }

        .status-error {
            color: #b42318;
            font-weight: 700;
        }

        nav {
            display: flex;
            flex-wrap: wrap;
            gap: 12px;
            margin-top: 24px;
        }

        a {
            color: #0b5cab;
            font-weight: 700;
        }
    </style>
</head>
<body>
<main>
    <h1>Apache con PHP listo</h1>
    <p>Este contenedor sirve PHP y puede hablar con el MySQL del stack Docker.</p>

    <dl>
        <dt>PHP</dt>
        <dd><?= htmlspecialchars(PHP_VERSION, ENT_QUOTES, 'UTF-8') ?></dd>
        <dt>MySQL</dt>
        <dd class="<?= $databaseStatus === 'OK' ? 'status-ok' : 'status-error' ?>">
            <?= htmlspecialchars($databaseStatus, ENT_QUOTES, 'UTF-8') ?>
        </dd>
        <dt>Detalle</dt>
        <dd><?= htmlspecialchars($databaseMessage, ENT_QUOTES, 'UTF-8') ?></dd>
    </dl>

    <nav aria-label="Links utiles">
        <a href="<?= htmlspecialchars($apiGatewayUrl, ENT_QUOTES, 'UTF-8') ?>">API Gateway</a>
        <a href="<?= htmlspecialchars($phpMyAdminUrl, ENT_QUOTES, 'UTF-8') ?>">phpMyAdmin</a>
    </nav>
</main>
</body>
</html>
