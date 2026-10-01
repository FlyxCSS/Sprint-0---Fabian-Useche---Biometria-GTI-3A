package com.example.fuseriv.aplicacionandroidble;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    // --------------------------------------------------------------
    // CONSTANTES
    // --------------------------------------------------------------

    private static final String ETIQUETA_LOG = ">>>>";

    private static final int CODIGO_PETICION_PERMISOS = 11223344;

    // Nombre BLE emitido por nuestra SparkFun
    private static final String NOMBRE_BEACON = "Fabian_GTI";

    // URL del servidor REST alojado en Plesk
    private static final String URL_MEDICION =
            "https://fuseriv.upv.edu.es/api/medicion";


    // --------------------------------------------------------------
    // BLUETOOTH
    // --------------------------------------------------------------

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner elEscanner;
    private ScanCallback callbackDelEscaneo;


    // --------------------------------------------------------------
    // CONTROL DE MEDICIONES
    // --------------------------------------------------------------

    /*
     * Guarda el último contador enviado al servidor.
     *
     * El contador está almacenado en el byte bajo del Major.
     *
     * Sirve para evitar enviar muchas veces la misma medición,
     * ya que un mismo iBeacon se recibe repetidamente durante
     * el intervalo de advertising.
     */
    private int ultimoContadorEnviado = -1;


    // --------------------------------------------------------------
    // PERMISOS
    // --------------------------------------------------------------

    /*
    ------------------------------------------------------------
    tengoPermisosBluetooth() --> B

    PRE:
    - Ninguna.

    POST:
    - Devuelve true si la aplicación tiene los permisos
      necesarios para realizar el escaneo BLE.
    - Devuelve false en caso contrario.
    ------------------------------------------------------------
    */
    private boolean tengoPermisosBluetooth() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            return ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED

                    &&

                    ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED

                    &&

                    ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED;
        }


        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }


    /*
    ------------------------------------------------------------
    pedirPermisosBluetooth() --> void

    PRE:
    - Ninguna.

    POST:
    - Solicita al usuario los permisos necesarios para realizar
      el escaneo BLE.
    ------------------------------------------------------------
    */
    private void pedirPermisosBluetooth() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    CODIGO_PETICION_PERMISOS
            );

        } else {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    CODIGO_PETICION_PERMISOS
            );
        }
    }


    // --------------------------------------------------------------
    // INICIALIZAR BLUETOOTH
    // --------------------------------------------------------------

    /*
    ------------------------------------------------------------
    inicializarBlueTooth() --> void

    PRE:
    - El dispositivo dispone de Bluetooth.

    POST:
    - Inicializa el adaptador Bluetooth y el escáner BLE.
    - Si faltan permisos, los solicita.
    ------------------------------------------------------------
    */
    @SuppressLint("MissingPermission")
    private void inicializarBlueTooth() {

        Log.d(
                ETIQUETA_LOG,
                "Inicializando Bluetooth..."
        );


        if (!tengoPermisosBluetooth()) {

            Log.d(
                    ETIQUETA_LOG,
                    "No tenemos permisos Bluetooth"
            );

            pedirPermisosBluetooth();

            return;
        }


        bluetoothAdapter =
                BluetoothAdapter.getDefaultAdapter();


        if (bluetoothAdapter == null) {

            Log.d(
                    ETIQUETA_LOG,
                    "ERROR: el móvil no tiene Bluetooth"
            );

            return;
        }


        if (!bluetoothAdapter.isEnabled()) {

            Log.d(
                    ETIQUETA_LOG,
                    "Bluetooth está desactivado. Actívalo manualmente."
            );

            return;
        }


        elEscanner =
                bluetoothAdapter.getBluetoothLeScanner();


        if (elEscanner == null) {

            Log.d(
                    ETIQUETA_LOG,
                    "ERROR: no se pudo obtener el escáner BLE"
            );

            return;
        }


        Log.d(
                ETIQUETA_LOG,
                "Bluetooth preparado correctamente"
        );
    }


    // --------------------------------------------------------------
    // COMPROBAR ESCÁNER
    // --------------------------------------------------------------

    /*
    ------------------------------------------------------------
    escanerPreparado() --> B

    PRE:
    - Ninguna.

    POST:
    - Devuelve true si el escáner BLE está disponible.
    - Intenta inicializar Bluetooth si todavía no se ha hecho.
    ------------------------------------------------------------
    */
    @SuppressLint("MissingPermission")
    private boolean escanerPreparado() {

        if (!tengoPermisosBluetooth()) {

            pedirPermisosBluetooth();

            return false;
        }


        if (bluetoothAdapter == null || elEscanner == null) {

            inicializarBlueTooth();
        }


        return elEscanner != null;
    }


    // --------------------------------------------------------------
    // BUSCAR TODOS LOS DISPOSITIVOS
    // --------------------------------------------------------------

    /*
    ------------------------------------------------------------
    buscarTodosLosDispositivosBTLE() --> void

    PRE:
    - Bluetooth está disponible y activado.
    - La aplicación dispone de los permisos necesarios.

    POST:
    - Inicia un escaneo de todos los dispositivos BLE cercanos.
    ------------------------------------------------------------
    */
    @SuppressLint("MissingPermission")
    private void buscarTodosLosDispositivosBTLE() {

        Log.d(
                ETIQUETA_LOG,
                "buscarTodosLosDispositivosBTLE()"
        );


        if (!escanerPreparado()) {
            return;
        }


        detenerBusquedaDispositivosBTLE();


        callbackDelEscaneo =
                new ScanCallback() {

                    @Override
                    public void onScanResult(
                            int callbackType,
                            ScanResult resultado
                    ) {

                        super.onScanResult(
                                callbackType,
                                resultado
                        );

                        mostrarInformacionDispositivoBTLE(
                                resultado
                        );
                    }


                    @Override
                    public void onBatchScanResults(
                            List<ScanResult> results
                    ) {

                        super.onBatchScanResults(results);

                        for (ScanResult resultado : results) {

                            mostrarInformacionDispositivoBTLE(
                                    resultado
                            );
                        }
                    }


                    @Override
                    public void onScanFailed(
                            int errorCode
                    ) {

                        super.onScanFailed(errorCode);

                        Log.d(
                                ETIQUETA_LOG,
                                "ERROR escaneando. Código: "
                                        + errorCode
                        );
                    }
                };


        ScanSettings settings =
                new ScanSettings.Builder()
                        .setScanMode(
                                ScanSettings.SCAN_MODE_LOW_LATENCY
                        )
                        .build();


        Log.d(
                ETIQUETA_LOG,
                "Empezamos a buscar todos los dispositivos BLE"
        );


        elEscanner.startScan(
                null,
                settings,
                callbackDelEscaneo
        );
    }


    // --------------------------------------------------------------
    // BUSCAR NUESTRA SPARKFUN
    // --------------------------------------------------------------

    /*
    ------------------------------------------------------------
    buscarEsteDispositivoBTLE(
        dispositivoBuscado: Texto
    ) --> void

    PRE:
    - Bluetooth está disponible y activado.
    - dispositivoBuscado contiene el nombre del dispositivo.

    POST:
    - Inicia un escaneo BLE filtrado por el nombre indicado.
    ------------------------------------------------------------
    */
    @SuppressLint("MissingPermission")
    private void buscarEsteDispositivoBTLE(
            final String dispositivoBuscado
    ) {

        Log.d(
                ETIQUETA_LOG,
                "Buscando: " + dispositivoBuscado
        );


        if (!escanerPreparado()) {
            return;
        }


        detenerBusquedaDispositivosBTLE();


        callbackDelEscaneo =
                new ScanCallback() {

                    @Override
                    public void onScanResult(
                            int callbackType,
                            ScanResult resultado
                    ) {

                        super.onScanResult(
                                callbackType,
                                resultado
                        );

                        mostrarInformacionDispositivoBTLE(
                                resultado
                        );
                    }


                    @Override
                    public void onBatchScanResults(
                            List<ScanResult> results
                    ) {

                        super.onBatchScanResults(results);

                        for (ScanResult resultado : results) {

                            mostrarInformacionDispositivoBTLE(
                                    resultado
                            );
                        }
                    }


                    @Override
                    public void onScanFailed(
                            int errorCode
                    ) {

                        super.onScanFailed(errorCode);

                        Log.d(
                                ETIQUETA_LOG,
                                "ERROR buscando "
                                        + dispositivoBuscado
                                        + ". Código: "
                                        + errorCode
                        );
                    }
                };


        // Filtro para recibir únicamente nuestro dispositivo.
        ScanFilter filtro =
                new ScanFilter.Builder()
                        .setDeviceName(
                                dispositivoBuscado
                        )
                        .build();


        List<ScanFilter> filtros =
                new ArrayList<>();

        filtros.add(filtro);


        ScanSettings settings =
                new ScanSettings.Builder()
                        .setScanMode(
                                ScanSettings.SCAN_MODE_LOW_LATENCY
                        )
                        .build();


        Log.d(
                ETIQUETA_LOG,
                "Iniciando búsqueda de "
                        + dispositivoBuscado
        );


        elEscanner.startScan(
                filtros,
                settings,
                callbackDelEscaneo
        );
    }


    // --------------------------------------------------------------
    // LEER DATOS DEL DISPOSITIVO
    // --------------------------------------------------------------

    /*
    ------------------------------------------------------------
    mostrarInformacionDispositivoBTLE(
        resultado: ScanResult
    ) --> void

    PRE:
    - resultado contiene una trama BLE válida.

    POST:
    - Muestra en Logcat la información recibida.
    - Si el dispositivo es Fabian_GTI, interpreta la trama
      como iBeacon.
    - Obtiene Major y Minor.
    - Envía una nueva medición al servidor cuando cambia
      el contador incluido en Major.
    ------------------------------------------------------------
    */
    @SuppressLint("MissingPermission")
    private void mostrarInformacionDispositivoBTLE(
            ScanResult resultado
    ) {

        if (resultado == null) {
            return;
        }


        if (resultado.getScanRecord() == null) {

            Log.d(
                    ETIQUETA_LOG,
                    "ScanRecord null"
            );

            return;
        }


        byte[] bytes =
                resultado
                        .getScanRecord()
                        .getBytes();


        if (bytes == null) {
            return;
        }


        if (bytes.length < 30) {

            Log.d(
                    ETIQUETA_LOG,
                    "Trama demasiado corta: "
                            + bytes.length
            );

            return;
        }


        BluetoothDevice dispositivo =
                resultado.getDevice();


        String nombre =
                dispositivo.getName();


        Log.d(
                ETIQUETA_LOG,
                "=================================="
        );

        Log.d(
                ETIQUETA_LOG,
                "DISPOSITIVO DETECTADO"
        );

        Log.d(
                ETIQUETA_LOG,
                "Nombre: " + nombre
        );

        Log.d(
                ETIQUETA_LOG,
                "Dirección: "
                        + dispositivo.getAddress()
        );

        Log.d(
                ETIQUETA_LOG,
                "RSSI: "
                        + resultado.getRssi()
        );

        Log.d(
                ETIQUETA_LOG,
                "Bytes: "
                        + Utilidades.bytesToHexString(bytes)
        );


        /*
         * No interpretamos cualquier dispositivo BLE como iBeacon
         * de nuestro proyecto.
         */
        if (!NOMBRE_BEACON.equals(nombre)) {

            Log.d(
                    ETIQUETA_LOG,
                    "Dispositivo ignorado: no es "
                            + NOMBRE_BEACON
            );

            Log.d(
                    ETIQUETA_LOG,
                    "=================================="
            );

            return;
        }


        // ----------------------------------------------------------
        // INTERPRETAR COMO IBEACON
        // ----------------------------------------------------------

        try {

            TramaIBeacon trama =
                    new TramaIBeacon(bytes);


            int major =
                    Utilidades.bytesToIntOK(
                            trama.getMajor()
                    );


            int minor =
                    Utilidades.bytesToIntOK(
                            trama.getMinor()
                    );


            /*
             * Major está formado por:
             *
             * byte alto -> tipo de medición
             * byte bajo -> contador
             */
            int tipoMedicion =
                    (major >> 8) & 0xFF;

            int contador =
                    major & 0xFF;


            Log.d(
                    ETIQUETA_LOG,
                    "UUID HEX: "
                            + Utilidades.bytesToHexString(
                            trama.getUUID()
                    )
            );


            Log.d(
                    ETIQUETA_LOG,
                    "UUID TEXTO: "
                            + Utilidades.bytesToString(
                            trama.getUUID()
                    )
            );


            Log.d(
                    ETIQUETA_LOG,
                    "MAJOR = " + major
            );


            Log.d(
                    ETIQUETA_LOG,
                    "TIPO MEDICION = " + tipoMedicion
            );


            Log.d(
                    ETIQUETA_LOG,
                    "CONTADOR = " + contador
            );


            Log.d(
                    ETIQUETA_LOG,
                    "MINOR = " + minor
            );


            Log.d(
                    ETIQUETA_LOG,
                    "TX POWER = "
                            + trama.getTxPower()
            );


            // ------------------------------------------------------
            // ENVIAR AL SERVIDOR
            // ------------------------------------------------------

            /*
             * Android recibe varias veces el mismo anuncio iBeacon.
             *
             * Solo enviamos la medición cuando aparece un contador
             * diferente al último que ya hemos enviado.
             */
            if (contador != ultimoContadorEnviado) {

                ultimoContadorEnviado = contador;

                Log.d(
                        ETIQUETA_LOG,
                        "Nueva medición. Se enviará al servidor."
                );


                /*
                 * Durante el Sprint 0 estamos trabajando con O3.
                 *
                 * El valor recibido está almacenado en Minor.
                 */
                enviarMedicionAlServidor(
                        "O3",
                        minor
                );

            } else {

                Log.d(
                        ETIQUETA_LOG,
                        "Medición repetida. No se envía nuevamente."
                );
            }


            Log.d(
                    ETIQUETA_LOG,
                    "=================================="
            );


        } catch (Exception e) {

            Log.d(
                    ETIQUETA_LOG,
                    "ERROR interpretando iBeacon: "
                            + e.getMessage()
            );
        }
    }


    // --------------------------------------------------------------
    // ENVIAR MEDICIÓN AL SERVIDOR REST
    // --------------------------------------------------------------

    /*
    ------------------------------------------------------------
    enviarMedicionAlServidor(
        tipo: Texto,
        valor: R
    ) --> void

    PRE:
    - tipo contiene el tipo de medición.
    - valor contiene el valor recibido mediante BLE.
    - Existe conexión a Internet.

    PROCESO:
    - Construir el cuerpo JSON de la petición.
    - Realizar POST contra /api/medicion.
    - Recibir la respuesta del servidor.

    POST:
    - La medición se envía al servidor REST.
    - La respuesta HTTP se muestra en Logcat.
    ------------------------------------------------------------
    */
    private void enviarMedicionAlServidor(
            String tipo,
            int valor
    ) {

        String cuerpoJSON =
                "{"
                        + "\"tipo\":\"" + tipo + "\","
                        + "\"valor\":" + valor
                        + "}";


        Log.d(
                ETIQUETA_LOG,
                "Enviando POST: " + cuerpoJSON
        );


        PeticionarioREST peticionario =
                new PeticionarioREST();


        peticionario.hacerPeticionREST(
                "POST",
                URL_MEDICION,
                cuerpoJSON,

                new PeticionarioREST.RespuestaREST() {

                    @Override
                    public void callback(
                            int codigo,
                            String cuerpo
                    ) {

                        Log.d(
                                ETIQUETA_LOG,
                                "RESPUESTA REST"
                        );

                        Log.d(
                                ETIQUETA_LOG,
                                "Código HTTP = " + codigo
                        );

                        Log.d(
                                ETIQUETA_LOG,
                                "Cuerpo = " + cuerpo
                        );
                    }
                }
        );
    }


    // --------------------------------------------------------------
    // DETENER ESCANEO
    // --------------------------------------------------------------

    /*
    ------------------------------------------------------------
    detenerBusquedaDispositivosBTLE() --> void

    PRE:
    - Puede existir un escaneo BLE activo.

    POST:
    - Detiene el escaneo BLE actual.
    ------------------------------------------------------------
    */
    @SuppressLint("MissingPermission")
    private void detenerBusquedaDispositivosBTLE() {

        if (callbackDelEscaneo == null) {
            return;
        }


        if (elEscanner == null) {
            return;
        }


        if (!tengoPermisosBluetooth()) {
            return;
        }


        elEscanner.stopScan(
                callbackDelEscaneo
        );


        callbackDelEscaneo = null;


        Log.d(
                ETIQUETA_LOG,
                "Escaneo detenido"
        );
    }


    // --------------------------------------------------------------
    // BOTONES
    // --------------------------------------------------------------

    /*
    ------------------------------------------------------------
    botonBuscarDispositivosBTLEPulsado(v: View) --> void

    POST:
    - Inicia la búsqueda de todos los dispositivos BLE.
    ------------------------------------------------------------
    */
    public void botonBuscarDispositivosBTLEPulsado(
            View v
    ) {

        buscarTodosLosDispositivosBTLE();
    }


    /*
    ------------------------------------------------------------
    botonBuscarNuestroDispositivoBTLEPulsado(v: View) --> void

    POST:
    - Busca únicamente el dispositivo Fabian_GTI.
    ------------------------------------------------------------
    */
    public void botonBuscarNuestroDispositivoBTLEPulsado(
            View v
    ) {

        buscarEsteDispositivoBTLE(
                NOMBRE_BEACON
        );
    }


    /*
    ------------------------------------------------------------
    botonDetenerBusquedaDispositivosBTLEPulsado(v: View) --> void

    POST:
    - Detiene el escaneo BLE.
    ------------------------------------------------------------
    */
    public void botonDetenerBusquedaDispositivosBTLEPulsado(
            View v
    ) {

        detenerBusquedaDispositivosBTLE();
    }


    // --------------------------------------------------------------
    // ON CREATE
    // --------------------------------------------------------------

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_main
        );


        Log.d(
                ETIQUETA_LOG,
                "onCreate()"
        );


        inicializarBlueTooth();
    }


    // --------------------------------------------------------------
    // RESULTADO DE PERMISOS
    // --------------------------------------------------------------

    /*
    ------------------------------------------------------------
    onRequestPermissionsResult(...)

    POST:
    - Comprueba si los permisos solicitados fueron concedidos.
    - Si se concedieron, inicializa Bluetooth.
    ------------------------------------------------------------
    */
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );


        if (requestCode == CODIGO_PETICION_PERMISOS) {

            boolean concedidos = true;


            if (grantResults.length == 0) {

                concedidos = false;

            } else {

                for (int resultado : grantResults) {

                    if (
                            resultado
                                    != PackageManager.PERMISSION_GRANTED
                    ) {

                        concedidos = false;

                        break;
                    }
                }
            }


            if (concedidos) {

                Log.d(
                        ETIQUETA_LOG,
                        "Permisos concedidos"
                );

                inicializarBlueTooth();

            } else {

                Log.d(
                        ETIQUETA_LOG,
                        "Permisos NO concedidos"
                );
            }
        }
    }


    // --------------------------------------------------------------
    // ON DESTROY
    // --------------------------------------------------------------

    @Override
    protected void onDestroy() {

        detenerBusquedaDispositivosBTLE();

        super.onDestroy();
    }
}