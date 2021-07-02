package com.hswatch.bluetooth;

import android.bluetooth.BluetoothSocket;

import com.hswatch.Utils;

import java.io.IOException;

class ThreadTestConnection extends Thread {

    private final MainServico mainServico;
    private final BluetoothSocket bluetoothSocket;
    private final int halfMinute;

    public ThreadTestConnection(MainServico mainServico) {
        this.mainServico = mainServico;
        this.bluetoothSocket = this.mainServico.getBluetoothSocket();
        this.halfMinute = 30*1000;
    }

    @Override
    public void run() {
        super.run();
        synchronized (this) {
            while (this.mainServico.isConnected() || this.bluetoothSocket.isConnected()) {
                try {
                    this.mainServico.write(Utils.delimitador);

                    this.wait(halfMinute);

                } catch (IOException | InterruptedException ioException) {
                    ioException.printStackTrace();
                    this.mainServico.connectionLost();
                    break;
                }
            }
        }
    }
}
