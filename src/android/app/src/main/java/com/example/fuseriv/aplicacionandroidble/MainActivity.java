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

    private static final String ETIQUETA_LOG = ">>>>";

    private static final int CODIGO_PETICION_PERMISOS = 11223344;

    // Nombre que emite nuestra SparkFun
    private static final String NOMBRE_BEACON = "Fabian_GTI";

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner elEscanner;
    private ScanCallback callbackDelEscaneo;


    // --------------------------------------------------------------
    // PERMISOS
    // --------------------------------------------------------------

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


    private void pedirPermisosBluetooth() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    CODIGO_PETICION_PERMISOS
            );

        } else {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    CODIGO_PETICION_PERMISOS
            );
        }
    }


    // --------------------------------------------------------------
    // INICIALIZAR BLUETOOTH
    // --------------------------------------------------------------

    @SuppressLint("MissingPermission")
    private void inicializarBlueTooth() {

        Log.d(ETIQUETA_LOG, "Inicializando Bluetooth...");

        if (!tengoPermisosBluetooth()) {

            Log.d(ETIQUETA_LOG, "No tenemos permisos Bluetooth");

            pedirPermisosBluetooth();

            return;
        }


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


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


        // FILTRO POR NOMBRE
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
                    "MINOR = " + minor
            );


            Log.d(
                    ETIQUETA_LOG,
                    "TX POWER = "
                            + trama.getTxPower()
            );


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
    // DETENER ESCANEO
    // --------------------------------------------------------------

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

    public void botonBuscarDispositivosBTLEPulsado(
            View v
    ) {

        buscarTodosLosDispositivosBTLE();
    }


    public void botonBuscarNuestroDispositivoBTLEPulsado(
            View v
    ) {

        buscarEsteDispositivoBTLE(
                NOMBRE_BEACON
        );
    }


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


        if (
                requestCode
                        == CODIGO_PETICION_PERMISOS
        ) {

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