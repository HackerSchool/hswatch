package io.hackerschool.hswatch_connection_module.connection_threads;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.UUID;

import io.hackerschool.hswatch_connection_module.HSWService;
import io.hackerschool.hswatch_connection_module.flags.HSWStatusFlag;

public class HSWThreadConnection extends Thread {

    /**
     * Current BluetoothSocket to connect the app to the BluetoothDevice
     */
    private final BluetoothSocket bluetoothSocket;

    /**
     * Main Service on which the thread is started and ended on and also where the thread gets the
     * current state of the connection and to connect to the other Java API frameworks
     */
    private final HSWService hswService;

    /**
     * The ThreadConnection's Constructor in which the connection is started with the Bluetooth
     * Socket from the Device. Besides that, it close any discovery that is happening on the phone,
     * because we found a device to connects to and we didn't need to discover any other new device.
     *
     * @param bluetoothDevice The BluetoothDevice which the user wants to connect to
     * @param hswService The main service where the connection will operate on and communicate with the
     *                other Java API frameworks
     */
    @SuppressLint("MissingPermission")
    public HSWThreadConnection(@NonNull BluetoothDevice bluetoothDevice, HSWService hswService) {

        // Try to get the BluetoothSocket from the BluetoothDevice and create a RFConnection using
        // the UUID from the Utils
        BluetoothSocket bluetoothSocket = null;
        try {
            UUID connectionUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(connectionUUID);
        } catch (IOException e) {
            e.printStackTrace();
            hswService.connectionFailed();
        }

        // Initializes final variables
        this.bluetoothSocket = bluetoothSocket;
        this.hswService = hswService;
        hswService.setBluetoothSocket(this.bluetoothSocket);

        // Change the current state the connection to Connecting
        HSWService.setConnectionStatus(HSWStatusFlag.STATE_CONNECTING);

    }

    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        // Stops the discovery of new Bluetooth Devices, so it can save battery resources
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

        // Tries to connect to the Bluetooth Device and then establishes a connection if
        // there wasn't any error
        try {
            this.bluetoothSocket.connect();
            this.hswService.connectionEstablish();

            // If there was any error, cancel the connection and tries to close the socket and, if an
            // error occurs, it must meant that there was an error out of the Application control, so
            // just print the error and don't do anything more
        } catch (NullPointerException | IOException exception) {
            exception.printStackTrace();
            this.hswService.setBluetoothSocket(null);
            try {
                this.bluetoothSocket.close();
            } catch (NullPointerException | IOException exception1) {
                exception1.printStackTrace();
            }

            this.hswService.connectionFailed();
        }
    }

    public void restart() {
        try {
            this.bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
