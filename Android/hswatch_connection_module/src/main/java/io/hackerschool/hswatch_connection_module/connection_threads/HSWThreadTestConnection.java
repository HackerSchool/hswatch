package io.hackerschool.hswatch_connection_module.connection_threads;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;

import io.hackerschool.hswatch_connection_module.HSWService;

public class HSWThreadTestConnection extends Thread {
    /**
     *
     */
    private final HSWService hswService;
    private final BluetoothSocket bluetoothSocket;

    public HSWThreadTestConnection(HSWService hswService) {
        this.hswService = hswService;
        this.bluetoothSocket = this.hswService.getBluetoothSocket();
    }

    @Override
    public void run() {
        super.run();
        synchronized (this) {
            while (this.hswService.isConnected() || this.bluetoothSocket.isConnected()) {
                try {
                    boolean result = this.hswService.testWrittingConnection();

                    if (result)
                        this.wait(30 * 1000);
                    else
                        throw new NullPointerException("There was no HSWThreadConnected class to send the message on!");
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                    this.hswService.threadInterrupted(this);
                    break;
                } catch (NullPointerException nullPointerException) {
                    nullPointerException.printStackTrace();
                    break;
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    this.hswService.connectionLost();
                    break;
                }
            }
        }
    }
}
