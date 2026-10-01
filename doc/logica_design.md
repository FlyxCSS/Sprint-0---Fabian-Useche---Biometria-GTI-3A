# Diseño de la Lógica de Negocio

## Diseño del Componente

MedicionEntrada = (
    tipo: Texto,
    valor: R
)

Medicion = (
    id: N,
    fecha: Texto,
    tipo: Texto,
    valor: R
)

guardarMedicion(
    tipo: Texto,
    valor: R
) --> Medicion

leerMedicion() --> Medicion | null


### guardarMedicion()

PRE:
- tipo existe.
- tipo no está vacío.
- valor existe.
- valor es numérico.

PROCESO:
- Eliminar espacios innecesarios del tipo.
- Validar que el tipo no esté vacío.
- Validar que el valor sea numérico.
- Convertir el valor a número real.
- Generar la fecha y hora actual en el backend.
- Insertar la medición en la tabla `Mediciones`.
- Obtener el identificador generado por la base de datos.

POST:
- Devuelve la medición guardada con:
  - id
  - fecha
  - tipo
  - valor
- Si los datos no son válidos, se produce un error de validación.


### leerMedicion()

PRE:
- Ninguna.

PROCESO:
- Consultar la tabla `Mediciones`.
- Ordenar las mediciones por `id` de forma descendente.
- Obtener únicamente la última medición almacenada.

POST:
- Devuelve la última medición disponible.
- Si no existen mediciones almacenadas, devuelve `null`.


## Aclaraciones del Diseño

- La lógica de negocio no realizará peticiones HTTP.
- La lógica de negocio no contendrá código de interfaz gráfica.
- La lógica de negocio recibirá una conexión PDO desde el exterior.
- La lógica de negocio no almacenará credenciales ni datos de conexión.
- La lógica de negocio será responsable de validar los datos recibidos.
- La fecha de la medición se generará en el backend en el momento de guardarla.
- La lógica de negocio accederá a la tabla `Mediciones` para guardar y recuperar datos.
- Durante el Sprint 0 solo se gestionarán los campos:
  - id
  - fecha
  - tipo
  - valor
- `leerMedicion()` devolverá únicamente la última medición almacenada.
- No se añadirá lógica adicional que no sea necesaria para la demostración del Sprint 0.


## Reglas Generales

- Lenguaje de programación: PHP.
- El código debe ser sencillo, legible y autoexplicativo.
- Cada función o método deberá incluir una cabecera con su diseño lógico entre líneas discontinuas.
- La comunicación HTTP pertenecerá al componente REST.
- Las credenciales de la base de datos no deberán almacenarse dentro de `LogicaNegocio.php`.
- Se deberán utilizar consultas preparadas para las operaciones de inserción.
- Se deberán generar pruebas para comprobar:
  - guardado correcto de una medición válida;
  - rechazo de una medición con tipo vacío;
  - rechazo de una medición con valor no numérico;
  - recuperación de la última medición;
  - comportamiento cuando no existen mediciones.