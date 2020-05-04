package com.hswatch.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;

public class NotificationListener extends NotificationListenerService {

    public static final String TAG = "hswatch.service.NOT";

    public static final byte[] separador = {0x03};
    public static final byte[] delimitador = {0x00};

//    BroadcastReceiver
    private NotificationListenerRecetor notificationListenerRecetor;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationListenerRecetor = new NotificationListenerRecetor();
        IntentFilter intentFilter = new IntentFilter(Servico.ACAO_SERVICO_NOT);
        registerReceiver(notificationListenerRecetor, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(notificationListenerRecetor);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);        
        NotificacaoRecebida(sbn.getNotification().extras.getString("android.title"),
                sbn.getNotification().extras.getString("android.text"),
                sbn.getNotification().category);
        Toast.makeText(getApplicationContext(), "Recebida mensagem: " + sbn.getNotification().extras.getString("android.text"), Toast.LENGTH_LONG).show();
    }

    private void NotificacaoRecebida(String titulo, String texto, String category) {
        if (titulo == null || texto == null) {
            return;
        }
        String[] msg = {
                "NOT",
                "SMS",
                DateFormat.getTimeInstance().format(new Date()).split(":")[0],
                DateFormat.getTimeInstance().format(new Date()).split(":")[1],
                titulo,
                texto
        };

        int indexelemento = 0;

        try {
            for (String elemento : msg){
                indexelemento ++;
                Intent notificacaoRecebida = new Intent(Servico.ACAO_SERVICO_NOT);
                notificacaoRecebida.putExtra(Servico.ELEMENTO_SERVICO_NOT, elemento.getBytes());
                sendBroadcast(notificacaoRecebida);
                if (elemento.equals(texto)) {
                    Intent notdelimitador = new Intent(Servico.ACAO_SERVICO_NOT)
                            .putExtra(Servico.ELEMENTO_SERVICO_NOT, delimitador);
                    sendBroadcast(notdelimitador);
                } else {
                    Intent notseparador = new Intent(Servico.ACAO_SERVICO_NOT)
                            .putExtra(Servico.ELEMENTO_SERVICO_NOT, separador);
                    sendBroadcast(notseparador);
                }
            }
        } catch (Exception e){
            Log.e(TAG, "Deu algum erro na notificação => " + indexelemento, e);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

    public static class NotificationListenerRecetor extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
}
