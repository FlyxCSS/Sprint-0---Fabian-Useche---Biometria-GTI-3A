# Diseño de la Interfaz Web

## Diseño del Componente

La interfaz web mostrará la última medición almacenada en el sistema.

La información mostrada será:

UltimaMedicion = (
    tipo: Texto,
    valor: R,
    fecha: Texto
)

La pantalla tendrá:

- Título: `Calidad del Aire`
- Apartado: `Última medición`
- Tipo de medición.
- Valor de la medición.
- Fecha de la medición.
- Hora de la medición.
- Botón `Actualizar`.


### cargarMedicion()

PRE:
- El servidor REST está disponible.

PROCESO:
- Realizar una petición GET a `/api/medicion`.
- Recibir la última medición en formato JSON.
- Separar la fecha y la hora recibidas.
- Mostrar los datos recibidos en la interfaz.

POST:
- La interfaz muestra:
  - tipo
  - valor
  - fecha
  - hora
- Si ocurre un error, se informa al usuario.


## Aclaraciones del Diseño

- La web será una interfaz sencilla para la demostración del Sprint 0.
- La web no accederá directamente a la base de datos.
- La web obtendrá los datos mediante el servidor REST.
- La web utilizará únicamente `GET /api/medicion`.
- La web mostrará únicamente la última medición almacenada.
- El botón `Actualizar` volverá a solicitar la última medición.
- No se añadirá lógica de negocio a la interfaz.
- No se modificarán mediciones desde la web.
- La interfaz se alojará en Plesk.
- La interfaz se dividirá en tres partes:
  - `index.html`: estructura de la página.
  - `css/estilos.css`: presentación visual.
  - `js/app.js`: comportamiento y comunicación con el servidor REST.


## Reglas Generales

- Lenguajes de programación:
  - HTML
  - CSS
  - JavaScript
- El código debe ser sencillo, legible y autoexplicativo.
- Las funciones deberán incluir una cabecera con su diseño lógico entre líneas discontinuas.
- La comunicación con el servidor se realizará mediante HTTP usando `fetch()`.
- La URL REST utilizada será `/api/medicion`.
- Se deberán realizar pruebas para comprobar:
  - carga correcta de una medición;
  - actualización mediante el botón;
  - comportamiento cuando el servidor devuelve `null`;
  - comportamiento cuando ocurre un error de comunicación.