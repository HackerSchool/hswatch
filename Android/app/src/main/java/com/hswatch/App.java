package com.hswatch;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class App extends Application {
    public static final String SERVICO_CHANNEL = "canalServico";
    public static final String CANAL_NOTIFACAO = "canalNotificacao";

    @Override
    public void onCreate() {
        super.onCreate();

        CanaisNoticacao();
    }

    private void CanaisNoticacao() {
        NotificationChannel canalServico = new NotificationChannel(SERVICO_CHANNEL,
                "Canal do Servico",
                NotificationManager.IMPORTANCE_DEFAULT);
        NotificationChannel canalNotificacoes = new NotificationChannel(CANAL_NOTIFACAO,"Canal de Notificacoes",
                NotificationManager.IMPORTANCE_HIGH);
        NotificationManager notmanager = getSystemService(NotificationManager.class);
        notmanager.createNotificationChannel(canalServico);
        notmanager.createNotificationChannel(canalNotificacoes);
    }
}
