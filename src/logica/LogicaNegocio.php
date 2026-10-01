<?php

class LogicaNegocio
{
    private PDO $conexion;

    public function __construct(PDO $conexion)
    {
        $this->conexion = $conexion;
    }


    /*
    ------------------------------------------------------------
    guardarMedicion(
        tipo: Texto,
        valor: R
    ) --> Medicion

    PRE:
    - tipo existe y no está vacío.
    - valor existe y es numérico.

    PROCESO:
    - Validar los datos recibidos.
    - Generar la fecha y hora actual.
    - Guardar la medición en la tabla Mediciones.
    - Recuperar la medición almacenada.

    POST:
    - Devuelve la medición guardada con:
      id, fecha, tipo y valor.
    ------------------------------------------------------------
    */
    public function guardarMedicion(string $tipo, $valor): array
    {
        $tipo = trim($tipo);

        if ($tipo === '') {
            throw new InvalidArgumentException(
                "El tipo de medición no puede estar vacío"
            );
        }

        if (!is_numeric($valor)) {
            throw new InvalidArgumentException(
                "El valor de la medición debe ser numérico"
            );
        }

        $valor = (float) $valor;

        $fecha = date('Y-m-d H:i:s');


        $sql = "
            INSERT INTO Mediciones (
                fecha,
                tipo,
                valor
            )
            VALUES (
                :fecha,
                :tipo,
                :valor
            )
        ";

        $sentencia = $this->conexion->prepare($sql);

        $sentencia->execute([
            ':fecha' => $fecha,
            ':tipo' => $tipo,
            ':valor' => $valor
        ]);


        $id = (int) $this->conexion->lastInsertId();


        return [
            'id' => $id,
            'fecha' => $fecha,
            'tipo' => $tipo,
            'valor' => $valor
        ];
    }


    /*
    ------------------------------------------------------------
    leerMedicion() --> Medicion | null

    PRE:
    - Ninguna.

    PROCESO:
    - Consultar la última medición almacenada en la tabla
      Mediciones.

    POST:
    - Devuelve la última medición.
    - Si no existen mediciones, devuelve null.
    ------------------------------------------------------------
    */
    public function leerMedicion(): ?array
    {
        $sql = "
            SELECT
                id,
                fecha,
                tipo,
                valor
            FROM Mediciones
            ORDER BY id DESC
            LIMIT 1
        ";

        $sentencia = $this->conexion->query($sql);

        $medicion = $sentencia->fetch(PDO::FETCH_ASSOC);


        if ($medicion === false) {
            return null;
        }


        $medicion['id'] = (int) $medicion['id'];
        $medicion['valor'] = (float) $medicion['valor'];

        return $medicion;
    }
}