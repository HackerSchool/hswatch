package com.hswatch;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.hswatch.App.CANAL_SERVICO;

public class Servico extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificacaoIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificacaoIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CANAL_SERVICO)
                .setContentTitle("Título")
                .setContentText("Conectado ao dispositivo").setSmallIcon(R.drawable.ic_bluetooth_black_24dp)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
        startForeground(3, notification);
        return START_STICKY;
    }
}
