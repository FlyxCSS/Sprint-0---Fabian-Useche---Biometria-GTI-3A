# Diseño del Microprocesador

## Diseño del Componente

El componente `microprocesador` se ejecuta sobre una SparkFun Pro nRF52840 Mini.

Su función en el Sprint 0 es generar una medición ficticia de O3, codificarla en una trama iBeacon y publicarla mediante Bluetooth Low Energy para que pueda ser recibida por la aplicación Android.

El flujo principal es:

```text
Medidor
   |
   | valor O3 en ppb
   v
Publicador
   |
   | Major + Minor
   v
EmisoraBLE
   |
   v
iBeacon
   |
   v
Android
```

### Tipos utilizados

```text
N     = número natural
Z     = número entero
R     = número real
B     = booleano
Text  = texto
```

Los tipos de medición disponibles se representan mediante:

```text
MedicionID = {
    O3,
    TEMPERATURA
}
```

En el Sprint 0 se utiliza únicamente `O3`.

---

### Conversión para depuración

```text
ppb: N --> convertirPpbAPpm() --> R
```

Convierte el valor de O3 expresado en ppb a ppm únicamente para mostrarlo de forma más legible en el Serial Monitor.

```text
ppm = ppb / 1000
```

Esta conversión no modifica el valor transmitido en el campo `Minor`.

---

### Medidor

```text
---------------- Medidor ----------------
|
| valor_o3_prueba_ppb: N
|
| iniciarMedidor() -->
|
N <-- medirO3() <--
|
-----------------------------------------
```

Responsabilidad:

Obtiene el valor de la medición.

Durante el Sprint 0 devuelve un valor ficticio de O3 expresado directamente en ppb.

Firmas lógicas:

```text
iniciarMedidor()

medirO3() --> N
```

---

### Publicador

```text
---------------------- Publicador ----------------------
|
| beacon_uuid: [N]_16
| emisora: EmisoraBLE
| rssi: Z
|
tipo: MedicionID, contador: N
    --> construirMajor() --> N
|
| encenderEmisora() -->
|
tipo: MedicionID, valor: N, contador: N, tiempo: N
    --> publicarMedida() --> N
|
--------------------------------------------------------
```

Responsabilidad:

Codifica la medición y solicita su publicación mediante Bluetooth Low Energy.

El campo `Major` se construye mediante:

```text
Major = (tipo << 8) | contador
```

Por tanto:

```text
byte alto = tipo de medición
byte bajo = contador
```

El campo `Minor` contiene directamente el valor de la medición en ppb.

Ejemplo:

```text
tipo O3 = 11
contador = 1
valor = 1234 ppb

Major = 2817
Minor = 1234
```

---

### EmisoraBLE

```text
--------------------- EmisoraBLE ---------------------
|
| nombre_emisora: Text
| fabricante_id: N
| tx_power: Z
|
nombre: Text, fabricante: N, potencia: Z
    --> EmisoraBLE() -->
|
| encenderEmisora() -->
|
B <-- estaAnunciando() <--
|
| detenerAnuncio() -->
|
uuid: [N]_16, major: N, minor: N, tx_power: Z
    --> emitirAnuncioIBeacon() -->
|
-------------------------------------------------------
```

Responsabilidad:

Gestiona la emisión Bluetooth Low Energy y construye el anuncio iBeacon.

Configuración utilizada:

```text
Nombre BLE: Fabian_GTI
Manufacturer ID: 0x004C
UUID: EPSG-GTI-PROY-3A
```

---

### PuertoSerie

```text
---------------- PuertoSerie ----------------
|
baudios: N --> PuertoSerie() -->
|
tiempo_maximo: N --> esperarDisponible() -->
|
valor --> escribir() -->
|
valor: R, decimales: N --> escribirDecimal() -->
|
----------------------------------------------
```

Responsabilidad:

Muestra información de depuración mediante Serial Monitor.

El puerto serie no es necesario para que el sistema funcione. Al arrancar se espera únicamente un tiempo máximo y después la ejecución continúa aunque no exista un ordenador conectado.

La salida permite comprobar:

```text
número de loop
tipo de medición
valor de O3 en ppm
contador
Major
Minor
intervalo de publicación
```

El valor obtenido desde `Medidor` está en ppb. La conversión a ppm se utiliza únicamente para mostrarlo en Serial Monitor y no modifica el valor transmitido.

---

### LED

```text
---------------- LED ----------------
|
| numero_led: Z
| encendido: B
|
numero: Z --> LED() -->
|
| encender() -->
|
| apagar() -->
|
| alternar() -->
|
tiempo: N --> brillar() -->
|
-------------------------------------
```

Responsabilidad:

Proporciona una indicación visual de que el microprocesador continúa ejecutándose.

El LED realiza un destello al comenzar cada nueva medición.

---

### Programa principal

La ejecución comienza en `setup()` y continúa mediante `loop()`:

```text
setup()
   |
   +--> esperar temporalmente al Serial Monitor
   +--> iniciar BLE
   +--> iniciar Medidor
   |
   v
loop()
   |
   +--> destello LED
   +--> medir O3 en ppb
   +--> convertir ppb a ppm para Serial
   +--> obtener contador
   +--> construir Major
   +--> mostrar datos por Serial
   +--> publicar iBeacon
   +--> incrementar número de loop
```

La trama enviada utiliza:

```text
Major = tipo de medición + contador
Minor = valor de O3 en ppb
```

Un mismo anuncio BLE puede recibirse varias veces en Android.

El contador incluido en el byte bajo de `Major` permite distinguir una nueva medición de las repeticiones del mismo anuncio.

El contador enviado ocupa un byte, por lo que puede tomar valores entre `0` y `255`.

---

### Prueba automática

La función `construirMajor()` dispone de una prueba automática mediante `static_assert`.

Se comprueba:

```text
O3 = 11
contador = 5

Major esperado = 2821
```

Si la función deja de producir ese resultado, la compilación falla.

La prueba no necesita hardware y se ejecuta automáticamente durante la compilación.

---

## Aclaraciones del Diseño

- La medición ficticia de O3 se introduce directamente en ppb.
- El campo `Minor` contiene exactamente el valor de la medición en ppb.
- La conversión de ppb a ppm se utiliza únicamente para mostrar el dato de forma legible en Serial Monitor.
- `O3` es el tipo utilizado actualmente.
- `TEMPERATURA` queda definido para permitir futuras ampliaciones.
- El campo `Major` contiene el tipo de medición en su byte alto y el contador en su byte bajo.
- El contador permite diferenciar nuevas mediciones de anuncios BLE repetidos.
- El microprocesador funciona aunque el Serial Monitor no esté conectado.
- El LED permite comprobar visualmente que la placa continúa ejecutándose.
- La SparkFun se comunica directamente únicamente con Android mediante Bluetooth Low Energy.
- La SparkFun no accede directamente al servidor REST, la lógica de negocio ni la base de datos.

## Reglas Generales

- Lenguaje de programación: C++ con Arduino y la librería Adafruit Bluefruit nRF52.
- La implementación debe mantenerse en `src/microprocesador/` y corresponder con este diseño.
- Cada fichero debe incluir una cabecera con nombre, descripción, fecha, autor, aportación y copyright.
- Cada función o método debe incluir su diseño lógico y una breve descripción dentro de un bloque de comentarios delimitado por líneas discontinuas.
- El código debe ser claro y autoexplicativo, evitando comentarios innecesarios.
- Las responsabilidades de medición, publicación BLE, comunicación Bluetooth, depuración y señalización visual deben mantenerse separadas.
- Las funciones de lógica que puedan probarse sin hardware deben disponer de pruebas automáticas reproducibles cuando resulte adecuado.
- El funcionamiento del microprocesador no debe depender de que exista un ordenador o Serial Monitor conectado.