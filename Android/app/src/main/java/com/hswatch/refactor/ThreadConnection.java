package com.hswatch.refactor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.hswatch.Utils;

import java.io.IOException;

//TODO(documentar)
public class ThreadConnection extends Thread {

    private final BluetoothSocket bluetoothSocket;

    private final Servico servico;

    public ThreadConnection(BluetoothDevice bluetoothDevice, Servico servico) {
        BluetoothSocket bluetoothSocket = null;
        try {
            bluetoothSocket = bluetoothDevice
                    .createRfcommSocketToServiceRecord(Utils.uid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.bluetoothSocket = bluetoothSocket;
        servico.setCurrentState(Servico.STATE_CONNECTING);
        this.servico = servico;
    }

    @Override
    public void run() {
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        try {
            bluetoothSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                bluetoothSocket.close();
                // TODO(red signal to the setup phase)
                // TODO(connectionFailed();)
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        servico.establishConnection();
    }

    public void cancel() {
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BluetoothSocket getBluetoothSocket() {
        return this.bluetoothSocket;
    }
}
