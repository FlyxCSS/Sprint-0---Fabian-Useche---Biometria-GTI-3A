package com.example.fuseriv.aplicacionandroidble;

import java.util.Arrays;

public class TramaIBeacon {

    private byte[] prefijo;
    private byte[] uuid;
    private byte[] major;
    private byte[] minor;

    private byte txPower;

    private byte[] advFlags;
    private byte[] advHeader;
    private byte[] companyID;

    private byte iBeaconType;
    private byte iBeaconLength;


    public TramaIBeacon(byte[] bytes) {

        if (bytes == null || bytes.length < 30) {

            throw new IllegalArgumentException(
                    "La trama iBeacon necesita al menos 30 bytes"
            );
        }


        prefijo =
                Arrays.copyOfRange(
                        bytes,
                        0,
                        9
                );


        uuid =
                Arrays.copyOfRange(
                        bytes,
                        9,
                        25
                );


        major =
                Arrays.copyOfRange(
                        bytes,
                        25,
                        27
                );


        minor =
                Arrays.copyOfRange(
                        bytes,
                        27,
                        29
                );


        txPower =
                bytes[29];


        advFlags =
                Arrays.copyOfRange(
                        prefijo,
                        0,
                        3
                );


        advHeader =
                Arrays.copyOfRange(
                        prefijo,
                        3,
                        5
                );


        companyID =
                Arrays.copyOfRange(
                        prefijo,
                        5,
                        7
                );


        iBeaconType =
                prefijo[7];


        iBeaconLength =
                prefijo[8];
    }


    public byte[] getPrefijo() {
        return prefijo;
    }


    public byte[] getUUID() {
        return uuid;
    }


    public byte[] getMajor() {
        return major;
    }


    public byte[] getMinor() {
        return minor;
    }


    public byte getTxPower() {
        return txPower;
    }


    public byte[] getAdvFlags() {
        return advFlags;
    }


    public byte[] getAdvHeader() {
        return advHeader;
    }


    public byte[] getCompanyID() {
        return companyID;
    }


    public byte getiBeaconType() {
        return iBeaconType;
    }


    public byte getiBeaconLength() {
        return iBeaconLength;
    }
}