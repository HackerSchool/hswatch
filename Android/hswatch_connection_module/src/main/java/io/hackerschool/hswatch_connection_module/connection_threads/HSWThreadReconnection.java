package io.hackerschool.hswatch_connection_module.connection_threads;

import android.content.SharedPreferences;

import io.hackerschool.hswatch_connection_module.HSWService;

public class HSWThreadReconnection extends Thread {
    private final HSWService hswService;

    public HSWThreadReconnection(HSWService hswService) {
        this.hswService = hswService;
    }

    @Override
    public void run() {
        super.run();
        synchronized (this) {
            while (this.hswService.isKeepConnection() && HSWService.isFlagReconnection()) {
                try {
                    this.hswService.setFlagError(false);
                    this.hswService.createConnection(this.hswService.getBluetoothDevice());

                    this.wait(60 * 1000);

                    if (Thread.interrupted())
                        throw new InterruptedException("Reconnection was interrupted!");

                } catch (InterruptedException interruptedException) {
                    this.hswService.threadInterrupted(this);
                    interruptedException.printStackTrace();
                    break;
                }
            }
        }
    }
}
