# Diseño del Servidor REST

## Diseño del Componente

POST /medicion

Entrada:
MedicionEntrada = (
    tipo: Texto,
    valor: R
)

Salida:
Medicion = (
    id: N,
    fecha: Texto,
    tipo: Texto,
    valor: R
)


GET /medicion

Entrada:
- Ninguna.

Salida:
Medicion | null


### POST /medicion

PRE:
- La petición utiliza el método HTTP POST.
- El cuerpo contiene datos en formato JSON.
- El JSON contiene:
  - tipo
  - valor

PROCESO:
- Leer el cuerpo JSON recibido.
- Comprobar que existen los campos `tipo` y `valor`.
- Pasar los datos a `LogicaNegocio.guardarMedicion()`.
- Obtener la medición guardada.
- Convertir el resultado a JSON.

POST:
- Devuelve la medición almacenada.
- Si los datos recibidos no son válidos, devuelve un error HTTP 400.
- Si ocurre un error interno, devuelve un error HTTP 500.


### GET /medicion

PRE:
- La petición utiliza el método HTTP GET.

PROCESO:
- Solicitar a `LogicaNegocio.leerMedicion()` la última medición.
- Convertir el resultado a JSON.

POST:
- Devuelve la última medición almacenada.
- Si no existen mediciones, devuelve `null`.
- Si ocurre un error interno, devuelve un error HTTP 500.


## Aclaraciones del Diseño

- El servidor REST será responsable únicamente de la comunicación HTTP.
- El servidor REST no contendrá lógica de negocio.
- El servidor REST no ejecutará consultas SQL directamente.
- El servidor REST utilizará `LogicaNegocio` para guardar y recuperar mediciones.
- Los datos intercambiados se enviarán en formato JSON.
- Durante el Sprint 0 solo existirán dos operaciones:
  - `POST /medicion`
  - `GET /medicion`
- El servidor REST devolverá respuestas JSON.
- La conexión a la base de datos se creará antes de instanciar `LogicaNegocio`.
- Las credenciales de la base de datos no estarán escritas directamente en el código público del repositorio.

## Reglas Generales

- Lenguaje de programación: PHP.
- El código debe ser sencillo, legible y autoexplicativo.
- Cada función o método deberá incluir una cabecera con su diseño lógico entre líneas discontinuas.
- La lógica de negocio deberá mantenerse separada de la comunicación HTTP.
- Las consultas SQL deberán realizarse únicamente a través de la lógica de negocio.
- Se utilizarán códigos HTTP adecuados:
  - 200 para peticiones correctas.
  - 201 para una medición creada correctamente.
  - 400 para datos incorrectos.
  - 404 para rutas inexistentes.
  - 500 para errores internos.
- Se deberán generar pruebas para comprobar:
  - POST correcto con tipo y valor válidos.
  - POST con datos incompletos.
  - POST con valor no numérico.
  - GET con mediciones existentes.
  - GET sin mediciones.