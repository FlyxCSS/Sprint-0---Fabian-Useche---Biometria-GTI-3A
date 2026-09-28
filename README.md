# Sprint 0 - Biometría GTI 3A

Proyecto de Biometría y Medio Ambiente.

## Objetivo

El objetivo del Sprint 0 es disponer de un prototipo básico capaz de enviar una medición desde un nodo sensor basado en SparkFun Pro nRF52840 Mini hasta una aplicación Android mediante Bluetooth Low Energy (BLE).

Posteriormente, la aplicación Android enviará la medición a un servidor REST, donde se almacenará en una base de datos y podrá consultarse desde una aplicación web.

## Flujo general

Sensor
-> SparkFun
-> BLE / iBeacon
-> Android
-> API REST
-> Lógica de negocio
-> Base de datos
-> Web

## Componentes del proyecto

- `src/microprocesador/`
  - Código del SparkFun y emisión BLE/iBeacon.

- `src/android/`
  - Aplicación Android para detectar los beacons y obtener las mediciones.

- `src/logica/`
  - Lógica de negocio del backend.

- `src/database/`
  - Scripts y diseño de la base de datos.

- `src/rest/`
  - Servidor REST y rutas de comunicación.

- `src/web/`
  - Aplicación web para mostrar las mediciones.

- `doc/`
  - Diseños de cada componente usando la notación de la asignatura.

## Sprint 0

En este sprint se trabaja principalmente en:

- ingeniería inversa del código proporcionado;
- diseño de los distintos componentes;
- generación de nuevo código a partir de los diseños;
- integración de Arduino, Android, servidor REST, base de datos y web;
- pruebas del sistema completo.

## Estado actual

Actualmente están implementadas las partes de microprocesador y Android.

El resto de componentes se irán añadiendo a medida que va avanzadno el Sprint 0.