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
                    boolean result = this.mainServico.write(Utils.delimitador);

                    if (result)
                        this.wait(halfMinute);
                    else
                        throw new NullPointerException("There was no ThreadConnected class to send the message on!");

                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                    this.mainServico.threadInterrupted(this);
                    break;
                } catch (NullPointerException nullPointerException) {
                    nullPointerException.printStackTrace();
                    break;
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    this.mainServico.connectionLost();
                    break;
                }
            }
        }
    }
}
