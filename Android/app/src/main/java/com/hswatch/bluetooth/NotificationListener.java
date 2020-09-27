package com.hswatch.bluetooth;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.hswatch.Constantes.ACAO_ATIVIDADE_NOTIFICACOES;
import static com.hswatch.Constantes.ACAO_NOTIFICACOES_ATIVIDADE;
import static com.hswatch.Constantes.ACAO_NOTIFICACOES_SERVICO;
import static com.hswatch.Constantes.ATIVIDADE_CHAVE;
import static com.hswatch.Constantes.DIRETIVA;
import static com.hswatch.Constantes.RECOLHER;
import static com.hswatch.Constantes.delimitador;
import static com.hswatch.Constantes.packagesNotFiltro;
import static com.hswatch.Constantes.separador;
import static com.hswatch.NotActivity.notAtividadeAtiva;

public class NotificationListener extends NotificationListenerService {

    public static final String TAG = "hswatch.service.NOT";

//    BroadcastReceiver
    private NotificationListenerRecetor notificationListenerRecetor;

<<<<<<< Updated upstream
=======
    public static List<List<String>> notificacoesGuardadas = new ArrayList<>();

    public static HashMap<String, String> packagesFiltro = new HashMap<String, String>(){{
        put("whatsapp", "com.whatsapp");
        put("instagram", "com.instagram.android");
        put("messenger", "com.facebook.orca");
        put("facebook", "com.facebook.katana");
    }};

>>>>>>> Stashed changes
    @Override
    public void onCreate() {
        super.onCreate();
        notificationListenerRecetor = new NotificationListenerRecetor();
        IntentFilter intentFilter = new IntentFilter(ACAO_NOTIFICACOES_SERVICO);
        intentFilter.addAction(ACAO_ATIVIDADE_NOTIFICACOES);
        registerReceiver(notificationListenerRecetor, intentFilter);
//        terPackagesNames();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(notificationListenerRecetor);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        Log.v(TAG, "Guardar notificação" + sbn.getPackageName());
        if (packagesNotFiltro.containsKey(sbn.getPackageName())) {
            guardarNot(sbn);
            NotificacaoRecebida(sbn.getNotification().extras.getString("android.title"),
                    sbn.getNotification().extras.getString("android.text"),
                    sbn.getNotification().category, sbn.getPackageName());
        }
    }

    private String obterChave(String valor) {
        for (String key : packagesFiltro.keySet()) {
            if (Objects.equals(valor, packagesFiltro.get(key))) {
                return key.toUpperCase();
            }
        }
        return valor;
    }

    private void guardarNot(StatusBarNotification sbn) {
//        infoNot = { nome, packageName, time_received, category, title, text }
//        Log.v(TAG, "Guardar notificação");

        List<String> notif = new ArrayList<String>(){{
//            Verificar se a app que mandou a notificação foi instalada pelo utilizador ou pelo sistema
            add(obterChave(sbn.getPackageName()));
//            Obter a packageName da notificação
            add(sbn.getPackageName());
//            Obter dia e hora da receção da notificação
            add(DateFormat.getDateInstance(DateFormat.SHORT, Locale.UK).format(new Date()) + " " +
                    DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.UK).format(new Date().getTime()));
//            Obter a categoria da notificação
            add(sbn.getNotification().category);
//            Obter o titulo da notificação
            add(sbn.getNotification().extras.getString("android.title"));
//            Obter o conteudo da notificação
            add(sbn.getNotification().extras.getString("android.text"));
        }};

        if (notAtividadeAtiva) {
            Intent mandarNot = new Intent(ACAO_NOTIFICACOES_ATIVIDADE);
            mandarNot.putExtra(DIRETIVA, notif.toArray(new String[0]));
            sendBroadcast(mandarNot);
        } else {
//        Guardar na lista para depois mandar para a atividade e guardar no Room
            notificacoesGuardadas.add(notif);
        }
    }

//    private void terPackagesNames() {
//        PackageManager pm = getPackageManager();
//        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
//        for (ApplicationInfo app : apps) {
//            if(pm.getLaunchIntentForPackage(app.packageName) != null) {
//                // apps with launcher intent
//                if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0 &&
//                        (app.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
//                    packagesPermitidas.add(app.packageName);
//                }
//            }
//        }
//    }

    public static void NotificacaoRecebida(String titulo, String texto, String category, String packageName) {
        if (titulo == null || texto == null) {
            return;
        }
        byte[][] mensagemNotificacao = {
                "NOT".getBytes(),
                separador,
                Objects.requireNonNull(packagesNotFiltro.get(packageName)).getBytes(),
                separador,
                DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.UK).format(new Date().getTime())
                        .split(":")[0].getBytes(),
                separador,
                DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.UK).format(new Date().getTime())
                        .split(":")[1].getBytes(),
                separador,
                titulo.getBytes(),
                separador,
                texto.getBytes(),
                delimitador
        };

        Servico.enviarMensagensRelogio(mensagemNotificacao);
//        int indexelemento = 0;
//        try {
//            for (byte[] elemento : mensagemNotificacao){
//                indexelemento ++;
//                Intent notificacaoRecebida = new Intent(ACAO_NOTIFICACOES_SERVICO);
//                notificacaoRecebida.putExtra(ELEMENTO_SERVICO_NOT, elemento.getBytes());
//                sendBroadcast(notificacaoRecebida);
//                if (elemento.equals(texto)) {
//                    Intent notdelimitador = new Intent(ACAO_NOTIFICACOES_SERVICO)
//                            .putExtra(ELEMENTO_SERVICO_NOT, delimitador);
//                    sendBroadcast(notdelimitador);
//                } else {
//                    Intent notseparador = new Intent(ACAO_NOTIFICACOES_SERVICO)
//                            .putExtra(ELEMENTO_SERVICO_NOT, separador);
//                    sendBroadcast(notseparador);
//                }
//            }
//        } catch (Exception e){
//            Log.e(TAG, "Deu algum erro na notificação => " + indexelemento, e);
//        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
//        Toast.makeText(getApplicationContext(), "Recebida mensagem: " + sbn.getNotification().category + " " + sbn.getPackageName(), Toast.LENGTH_LONG).show();
//        Log.v(TAG, sbn.getNotification().extras.getString("android.title") + "\t" +
//                sbn.getNotification().extras.getString("android.text") + "\t" +
//                sbn.getNotification().category + "\t" + sbn.getPackageName());
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        String notificationListenerString = Settings.Secure.getString(this.getContentResolver(),"enabled_notification_listeners");
        //Check notifications access permission
        if (!(notificationListenerString == null || !notificationListenerString.contains(getPackageName())))
        {
            requestRebind(ComponentName.createRelative(this.getApplicationContext().getPackageName(), "ListenerNotificationTest"));
        }
    }

    public class NotificationListenerRecetor extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String acao = intent.getAction();
            if (acao == null) {
                return;
            }
            if (Objects.equals(ACAO_ATIVIDADE_NOTIFICACOES, acao)) {
                String chave = intent.getStringExtra(ATIVIDADE_CHAVE);
                if (chave == null) {
                    return;
                }
                if (RECOLHER.equals(chave)) {
                    if (!notificacoesGuardadas.isEmpty()) {
                        for (List<String> not : notificacoesGuardadas) {
                            Intent notificaoesAtividade = new Intent(ACAO_NOTIFICACOES_ATIVIDADE);
                            notificaoesAtividade.putExtra(DIRETIVA, not.toArray(new String[0]));
                            sendBroadcast(notificaoesAtividade);
                        }
                        notificacoesGuardadas.clear();
                    }
                }
            }
        }
    }
}
