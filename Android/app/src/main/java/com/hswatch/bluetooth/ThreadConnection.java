package com.hswatch.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.hswatch.Utils;

import java.io.IOException;

import androidx.annotation.NonNull;

//TODO(documentar)
public class ThreadConnection extends Thread {

    /**
     * Current BluetoothSocket to connect the app to the BluetoothDevice
     */
    private final BluetoothSocket bluetoothSocket;

    /**
     * Main Service on which the thread is started and ended on and also where the thread gets the
     * current state of the connection and to connect to the other Java API frameworks
     */
    private final MainServico mainServico;

    /**
     * The ThreadConnection's Constructor in which the connection is started with the Bluetooth
     * Socket from the Device. Besides that, it close any discovery that is happening on the phone,
     * because we found a device to connects to and we didn't need to discover any other new device.
     *
     * @param bluetoothDevice The BluetoothDevice which the user wants to connect to
     * @param mainServico The main service where the connection will operate on and communicate with the
     *                other Java API frameworks
     */
    public ThreadConnection(@NonNull BluetoothDevice bluetoothDevice, MainServico mainServico) {

        // Try to get the BluetoothSocket from the BluetoothDevice and create a RFConnection using
        // the UUID from the Utils
        BluetoothSocket bluetoothSocket = null;
        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(Utils.uid);
        } catch (IOException e) {
            e.printStackTrace();
            mainServico.connectionFailed();
        }

        // Initializes final variables
        this.bluetoothSocket = bluetoothSocket;
        this.mainServico = mainServico;
        mainServico.setBluetoothSocket(this.bluetoothSocket);

        // Change the current state the connection to Connecting
        mainServico.setCurrentState(MainServico.STATE_CONNECTING);

    }

    @Override
    public void run() {
        // Stops the discovery of new Bluetooth Devices, so it can save battery resources
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

        // Tries to connect to the Bluetooth Device and then establishes a connection if
        // there wasn't any error
        try {
            this.bluetoothSocket.connect();
            this.mainServico.connectionEstablish();

        // If there was any error, cancel the connection and tries to close the socket and, if an
        // error occurs, it must meant that there was an error out of the Application control, so
        // just print the error and don't do anything more
        } catch (NullPointerException | IOException exception) {
            exception.printStackTrace();
            this.mainServico.setBluetoothSocket(null);
            try {
                this.bluetoothSocket.close();
            } catch (NullPointerException | IOException exception1) {
                exception1.printStackTrace();
            }

            this.mainServico.connectionFailed();
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
