package com.hswatch.bluetooth;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

class ThreadReconnection extends Thread {


    private final MainServico mainServico;

    private final long oneMinute;

    public ThreadReconnection(MainServico mainServico) {
        this.mainServico = mainServico;
        this.oneMinute = 60*1000;
    }

    @Override
    public void run() {
        super.run();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                this.mainServico.getCurrentContext()
        );
        synchronized (this) {
            while (sharedPreferences.getBoolean("connection", true) && MainServico.isFlagReconnection()) {
                try {
                    this.mainServico.setFlagError(false);
                    this.mainServico.createConection(this.mainServico.getBluetoothDevice());

                    this.wait(oneMinute);

//                    Thread.sleep(60000);

                    if (Thread.interrupted())
                        throw new InterruptedException("Reconnection was interrupted!");

                } catch(InterruptedException interruptedException){
                    this.mainServico.threadInterrupted(this);
                    interruptedException.printStackTrace();
                    break;
                }
            }
        }
    }
}
