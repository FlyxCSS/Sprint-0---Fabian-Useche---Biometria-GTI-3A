# Diseño de la Base de Datos

## Diseño del Componente

Mediciones = (
    id: N,
    fecha: Texto,
    tipo: Texto,
    valor: R
)

- id: clave primaria.
- fecha: fecha y hora de la medición.
- tipo: tipo de medición recibida.
- valor: valor numérico de la medición.

Cada fila de la tabla representa una medición recibida desde el móvil.

Ejemplo:

| id | fecha               | tipo | valor |
|----|---------------------|------|-------|
| 1  | 2026-09-28 16:25:00 | O3   | 0.032 |
| 2  | 2026-09-28 16:26:00 | O3   | 0.035 |
| 3  | 2026-09-28 16:27:00 | O3   | 0.031 |

## Aclaraciones del Diseño

- La base de datos tendrá una única tabla llamada `Mediciones`.
- `id` será único y autoincremental.
- No se añadirán más tablas ni campos durante el Sprint 0.
- La fecha almacenará tanto la fecha como la hora.
- `tipo` permitirá indicar el tipo de medición, por ejemplo `O3`.
- `valor` almacenará el valor numérico de la medición.
- La lógica de negocio será la responsable de solicitar el guardado y la lectura de datos.
- La base de datos no contendrá lógica de negocio.

## Reglas Generales

- Lenguaje de implementación: SQL.
- El código deberá ser sencillo y legible.
- La implementación deberá respetar exactamente la tabla definida en este diseño.
- No se añadirán campos o tablas que no aparezcan en el diseño.
- Se incluirán consultas de prueba para comprobar:
  - inserción de una medición;
  - lectura de mediciones;
  - recuperación de la última medición almacenada.