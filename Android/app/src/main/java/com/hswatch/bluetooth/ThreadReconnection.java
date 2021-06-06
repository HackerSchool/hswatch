package com.hswatch.bluetooth;

import android.content.Context;
import android.content.SharedPreferences;

import static com.hswatch.Utils.HISTORY_SHARED_PREFERENCES;

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
        SharedPreferences sharedPreferences = this.mainServico.getCurrentContext()
                .getSharedPreferences(HISTORY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        synchronized (this) {
            while (sharedPreferences.getBoolean("connection", true) && this.mainServico.isFlagReconnection()) {
                try {
                    this.mainServico.setFlagError(false);
                    this.mainServico.createConection(this.mainServico.getBluetoothDevice());

                    this.wait(oneMinute);

//                    Thread.sleep(60000);

                    if (Thread.interrupted()) {
                        throw new InterruptedException("Reconnection was interrupted!");
                    }

                } catch(InterruptedException interruptedException){
                    this.mainServico.threadInterrupted(this);
                    interruptedException.printStackTrace();
                }
            }
        }
    }
}
