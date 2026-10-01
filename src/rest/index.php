<?php

require_once __DIR__ . '/../logica/LogicaNegocio.php';

header('Content-Type: application/json; charset=utf-8');

/*
------------------------------------------------------------
crearConexion() --> PDO

PRE:
- Existen las variables de entorno necesarias para conectar
  con la base de datos.

POST:
- Devuelve una conexión PDO válida.
- Si falla la conexión, lanza una excepción.
------------------------------------------------------------
*/
function crearConexion(): PDO
{
    // En Plesk se sustituyen estos valores por las credenciales reales.
    // En GitHub se mantienen valores genéricos para no publicar credenciales.
    $host = 'DB_HOST';
    $nombreBD = 'DB_NAME';
    $usuario = 'DB_USER';
    $contrasena = 'DB_PASSWORD';

    if (!$host || !$nombreBD || !$usuario) {
        throw new Exception(
            'Faltan variables de entorno para conectar con la base de datos'
        );
    }

    $dsn =
        "mysql:host={$host};" .
        "dbname={$nombreBD};" .
        "charset=utf8mb4";

    return new PDO(
        $dsn,
        $usuario,
        $contrasena,
        [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
        ]
    );
}


/*
------------------------------------------------------------
responderJSON(
    datos: cualquiera,
    codigoHTTP: N
) --> void

PRE:
- codigoHTTP contiene un código HTTP válido.

POST:
- Envía una respuesta JSON al cliente.
------------------------------------------------------------
*/
function responderJSON($datos, int $codigoHTTP = 200): void
{
    http_response_code($codigoHTTP);

    echo json_encode(
        $datos,
        JSON_UNESCAPED_UNICODE
    );

    exit;
}


/*
------------------------------------------------------------
obtenerRuta() --> Texto

PRE:
- Existe una petición HTTP.

POST:
- Devuelve la ruta solicitada.
------------------------------------------------------------
*/
function obtenerRuta(): string
{
    $ruta = parse_url(
        $_SERVER['REQUEST_URI'],
        PHP_URL_PATH
    );

    return rtrim($ruta, '/');
}


try {

    $conexion = crearConexion();

    $logica = new LogicaNegocio($conexion);

    $metodo = $_SERVER['REQUEST_METHOD'];

    $ruta = obtenerRuta();


    /*
    --------------------------------------------------------
    POST /medicion
    --------------------------------------------------------
    */
    if ($metodo === 'POST' && str_ends_with($ruta, '/medicion')) {

        $contenido = file_get_contents('php://input');

        $datos = json_decode($contenido, true);

        if (!is_array($datos)) {
            responderJSON(
                [
                    'error' => 'El cuerpo debe contener JSON válido'
                ],
                400
            );
        }

        if (!array_key_exists('tipo', $datos)) {
            responderJSON(
                [
                    'error' => 'Falta el campo tipo'
                ],
                400
            );
        }

        if (!array_key_exists('valor', $datos)) {
            responderJSON(
                [
                    'error' => 'Falta el campo valor'
                ],
                400
            );
        }

        try {

            $medicion = $logica->guardarMedicion(
                $datos['tipo'],
                $datos['valor']
            );

            responderJSON(
                $medicion,
                201
            );

        } catch (InvalidArgumentException $e) {

            responderJSON(
                [
                    'error' => $e->getMessage()
                ],
                400
            );
        }
    }


    /*
    --------------------------------------------------------
    GET /medicion
    --------------------------------------------------------
    */
    if ($metodo === 'GET' && str_ends_with($ruta, '/medicion')) {

        $medicion = $logica->leerMedicion();

        responderJSON(
            $medicion,
            200
        );
    }


    responderJSON(
        [
            'error' => 'Ruta no encontrada'
        ],
        404
    );


} catch (Throwable $e) {

    responderJSON(
        [
            'error' => 'Error interno del servidor'
        ],
        500
    );
}