package com.hswatch.refactor;

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
    private final Servico servico;

    /**
     * The ThreadConnection's Constructor in which the connection is started with the Bluetooth
     * Socket from the Device. Besides that, it close any discovery that is happening on the phone,
     * because we found a device to connects to and we didn't need to discover any other new device.
     *
     * @param bluetoothDevice The BluetoothDevice which the user wants to connect to
     * @param servico The main service where the connection will operate on and communicate with the
     *                other Java API frameworks
     */
    public ThreadConnection(@NonNull BluetoothDevice bluetoothDevice, Servico servico) {

        // Try to get the BluetoothSocket from the BluetoothDevice and create a RFConnection using
        // the UUID from the Utils
        BluetoothSocket bluetoothSocket = null;
        try {
            bluetoothSocket = bluetoothDevice
                    .createRfcommSocketToServiceRecord(Utils.uid);
        } catch (IOException e) {
            e.printStackTrace();
            servico.connectionFailed();
        }

        // Initializes final variables
        this.bluetoothSocket = bluetoothSocket;
        this.servico = servico;

        // Change the current state the connection to Connecting
        servico.setCurrentState(Servico.STATE_CONNECTING);
    }

    @Override
    public void run() {
        if (BluetoothAdapter.getDefaultAdapter().cancelDiscovery()) {
            try {
                this.bluetoothSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    this.bluetoothSocket.close();

                    // TODO(red signal to the setup phase)

                    this.servico.connectionFailed();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            this.servico.establishConnection();
        } else {
            // Some error occur on the cancelling the Discovery
            this.servico.cancelDiscoveryFailed();
        }
    }

    public void cancel() {
        try {
            this.bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BluetoothSocket getBluetoothSocket() {
        return this.bluetoothSocket;
    }
}
