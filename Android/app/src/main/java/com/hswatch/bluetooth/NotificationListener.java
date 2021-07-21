package com.hswatch.bluetooth;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;

import static com.hswatch.NotActivity.notAtividadeAtiva;
import static com.hswatch.Utils.ACAO_ATIVIDADE_NOTIFICACOES;
import static com.hswatch.Utils.ACAO_NOTIFICACOES_ATIVIDADE;
import static com.hswatch.Utils.ACAO_NOTIFICACOES_SERVICO;
import static com.hswatch.Utils.ACTIVITY_KEY;
import static com.hswatch.Utils.DIRETIVA;
import static com.hswatch.Utils.NOT_INDICATOR;
import static com.hswatch.Utils.RECOLHER;
import static com.hswatch.Utils.WHATSAPP_NAME;
import static com.hswatch.Utils.WHATSAPP_WEB;
import static com.hswatch.Utils.packagesNotFilter;
import static com.hswatch.Utils.packagesNotIndicator;

public class NotificationListener extends NotificationListenerService {

    // BroadcastReceiver
    private NotificationListenerReceiver notificationListenerReceiver;

    // Filter variables
    /**
     * Notifications that the user already received and so it isn't send it twice to the Bluetooth
     * Device
     */
    private final List<List<String>> notificationsReceived = new ArrayList<>();

    /**
     * List of notifications which were sent to the Bluetooth Device
     */
    public static final List<List<String>> notificationsSaved = new ArrayList<>();


    @Override
    public void onCreate() {
        super.onCreate();
        notificationListenerReceiver = new NotificationListenerReceiver();
        IntentFilter intentFilter = new IntentFilter(ACAO_NOTIFICACOES_SERVICO);
        intentFilter.addAction(ACAO_ATIVIDADE_NOTIFICACOES);
        registerReceiver(notificationListenerReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(notificationListenerReceiver);
    }

    // Method called when the user receives a notification
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        if (packagesNotFilter.containsValue(sbn.getPackageName())) {
            sendNotification(sbn);
        }
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

    /**
     * Send notification to the MainService and for the Notification Activity. But also, filter the
     * notification in case if it's a repetitive one
     * @param sbn
     */
    private void sendNotification(StatusBarNotification sbn) {
        // creates a list of strings with the information needed
        List<String> currentNotification = getNotificationInformation(sbn);

        // Returns if it is a repeated notification
        if (WHATSAPP_WEB.equals(currentNotification.get(0)) ||
                WHATSAPP_NAME.equals(currentNotification.get(0)) ||
                this.notificationsReceived.contains(currentNotification) ||
                currentNotification.contains(null)) {
            return;
        }

        updateNotificationsReceivedList(currentNotification);

        List<String> message = sendMessage(currentNotification);

        if (notAtividadeAtiva) {
            Intent sendNot = new Intent(ACAO_NOTIFICACOES_ATIVIDADE);
            sendNot.putExtra(DIRETIVA, message.toArray(new String[0]));
            sendBroadcast(sendNot);
        } else {
//        Guardar na lista para depois mandar para a atividade e guardar no Room
            notificationsSaved.add(message);
        }

        MainServico.sendNotification(message);
    }

    @NonNull
    private static List<String> sendMessage(@NonNull List<String> notification) {
        List<String> message = new ArrayList<>();
        message.add(NOT_INDICATOR);
        message.add(packagesNotIndicator.get(notification.get(2)));
        message.add(DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.UK).format(new Date().getTime())
                .split(":")[0]);
        message.add(DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.UK).format(new Date().getTime())
                .split(":")[1]);
        message.add(notification.get(0));
        message.add(notification.get(1));

        return message;
    }

    private void updateNotificationsReceivedList(List<String> currentNotification) {
        this.notificationsReceived.add(currentNotification);
        if (this.notificationsReceived.size() > 10) {
            this.notificationsReceived.remove(0);
        }
    }

    @NonNull
    private List<String> getNotificationInformation(@NonNull StatusBarNotification sbn) {
        List<String> information = new ArrayList<>();
        information.add(sbn.getNotification().extras.getString("android.title"));
        information.add(sbn.getNotification().extras.getString("android.text"));
        information.add(sbn.getPackageName());
        information.add((new Date()).toString());
        return information;
    }

    public static void SMSReceived(String title, String text) {
        List<String> message = sendMessage(new ArrayList<String>(){{
            add(title);
            add(text);
            add(packagesNotFilter.get("sms"));
        }});

        MainServico.sendNotification(message);
    }

    public class NotificationListenerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String acao = intent.getAction();
            if (acao == null) {
                return;
            }
            if (Objects.equals(ACAO_ATIVIDADE_NOTIFICACOES, acao)) {
                String chave = intent.getStringExtra(ACTIVITY_KEY);
                if (chave == null) {
                    return;
                }
                if (RECOLHER.equals(chave)) {
                    if (!notificationsSaved.isEmpty()) {
                        for (List<String> not : notificationsSaved) {
                            Intent notificaoesAtividade = new Intent(ACAO_NOTIFICACOES_ATIVIDADE);
                            notificaoesAtividade.putExtra(DIRETIVA, not.toArray(new String[0]));
                            sendBroadcast(notificaoesAtividade);
                        }
                        notificationsSaved.clear();
                    }
                }
            }
        }
    }



    //
//    public static final String TAG = "hswatch.service.NOT";
//
////    BroadcastReceiver
//    private NotificationListenerRecetor notificationListenerRecetor;
//
//    public static List<List<String>> notificacoesGuardadas = new ArrayList<>();
//
//    public static HashMap<String, String> packagesFiltro = new HashMap<String, String>(){{
//        put("whatsapp", "com.whatsapp");
//        put("instagram", "com.instagram.android");
//        put("messenger", "com.facebook.orca");
//        put("facebook", "com.facebook.katana");
//    }};
//
//    private List<String> receiveddNotifications = new ArrayList<>();
//
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        notificationListenerRecetor = new NotificationListenerRecetor();
//        IntentFilter intentFilter = new IntentFilter(ACAO_NOTIFICACOES_SERVICO);
//        intentFilter.addAction(ACAO_ATIVIDADE_NOTIFICACOES);
//        registerReceiver(notificationListenerRecetor, intentFilter);
////        terPackagesNames();
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return super.onBind(intent);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(notificationListenerRecetor);
//    }
//
//    @Override
//    public void onNotificationPosted(StatusBarNotification sbn) {
//        super.onNotificationPosted(sbn);
////        Log.v(TAG, "Guardar notificação" + sbn.getPackageName());
//        // Filtro da notificação
//        if (packagesNotFiltro.containsKey(sbn.getPackageName())) {
//            guardarNot(sbn);
//
////        TODO(I'm dumb e esqueci-me que posso fazer o toString() para ler a notificação)
//
//            NotificacaoRecebida(sbn.getNotification().extras.getString("android.title"),
//                    sbn.getNotification().extras.getString("android.text"),
//                    sbn.getPackageName());
//        }
//    }
//
//    private String obterChave(String valor) {
//        for (String key : packagesFiltro.keySet()) {
//            if (Objects.equals(valor, packagesFiltro.get(key))) {
//                return key.toUpperCase();
//            }
//        }
//        return valor;
//    }
//
//    private void guardarNot(StatusBarNotification sbn) {
////        infoNot = { nome, packageName, time_received, category, title, text }
////        Log.v(TAG, "Guardar notificação");
//
//        List<String> notif = new ArrayList<String>(){{
////            Verificar se a app que mandou a notificação foi instalada pelo utilizador ou pelo sistema
//            add(obterChave(sbn.getPackageName()));
////            Obter a packageName da notificação
//            add(sbn.getPackageName());
////            Obter dia e hora da receção da notificação
//            add(DateFormat.getDateInstance(DateFormat.SHORT, Locale.UK).format(new Date()) + " " +
//                    DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.UK).format(new Date().getTime()));
////            Obter a categoria da notificação
//            add(sbn.getNotification().category);
////            Obter o titulo da notificação
//            add(sbn.getNotification().extras.getString("android.title"));
////            Obter o conteudo da notificação
////            add(sbn.getNotification().extras.getString("android.text"));
//            add(sbn.getNotification().extras.getString("android.text") + "\n\n" +
//                    sbn.toString() + "\n\n" + sbn.getNotification().extras.toString());
//        }};
//
//        if (notAtividadeAtiva) {
//            Intent mandarNot = new Intent(ACAO_NOTIFICACOES_ATIVIDADE);
//            mandarNot.putExtra(DIRETIVA, notif.toArray(new String[0]));
//            sendBroadcast(mandarNot);
//        } else {
////        Guardar na lista para depois mandar para a atividade e guardar no Room
//            notificacoesGuardadas.add(notif);
//        }
//    }
//
////    private void terPackagesNames() {
////        PackageManager pm = getPackageManager();
////        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
////        for (ApplicationInfo app : apps) {
////            if(pm.getLaunchIntentForPackage(app.packageName) != null) {
////                // apps with launcher intent
////                if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0 &&
////                        (app.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
////                    packagesPermitidas.add(app.packageName);
////                }
////            }
////        }
////    }
//
//    public static void NotificacaoRecebida(String titulo, String texto, String packageName) {
//        if (titulo == null || texto == null) {
//            return;
//        }
//        if (filterNot(titulo, texto, packageName)) {
//            List<String> mensagemNotificacao = new ArrayList<>();
//            mensagemNotificacao.add(NOT_INDICATOR);
//            mensagemNotificacao.add(Objects.requireNonNull(packagesNotFiltro.get(packageName)));
//            mensagemNotificacao.add(DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.UK).format(new Date().getTime())
//                    .split(":")[0]);
//            mensagemNotificacao.add(DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.UK).format(new Date().getTime())
//                    .split(":")[1]);
//            mensagemNotificacao.add(titulo);
//            mensagemNotificacao.add(texto);
//
//            MainServico.sendNotification(mensagemNotificacao);
//        }
//
//
////        Servico.enviarMensagensRelogio(mensagemNotificacao);
////        int indexelemento = 0;
////        try {
////            for (byte[] elemento : mensagemNotificacao){
////                indexelemento ++;
////                Intent notificacaoRecebida = new Intent(ACAO_NOTIFICACOES_SERVICO);
////                notificacaoRecebida.putExtra(ELEMENTO_SERVICO_NOT, elemento.getBytes());
////                sendBroadcast(notificacaoRecebida);
////                if (elemento.equals(texto)) {
////                    Intent notdelimitador = new Intent(ACAO_NOTIFICACOES_SERVICO)
////                            .putExtra(ELEMENTO_SERVICO_NOT, delimitador);
////                    sendBroadcast(notdelimitador);
////                } else {
////                    Intent notseparador = new Intent(ACAO_NOTIFICACOES_SERVICO)
////                            .putExtra(ELEMENTO_SERVICO_NOT, separador);
////                    sendBroadcast(notseparador);
////                }
////            }
////        } catch (Exception e){
////            Log.e(TAG, "Deu algum erro na notificação => " + indexelemento, e);
////        }
//    }
//
//    private static boolean filterNot(String titulo, String texto, String packageName) {
//
//        return false;
//    }
//
//    @Override
//    public void onNotificationRemoved(StatusBarNotification sbn) {
//        super.onNotificationRemoved(sbn);
////        Toast.makeText(getApplicationContext(), "Recebida mensagem: " + sbn.getNotification().category + " " + sbn.getPackageName(), Toast.LENGTH_LONG).show();
////        Log.v(TAG, sbn.getNotification().extras.getString("android.title") + "\t" +
////                sbn.getNotification().extras.getString("android.text") + "\t" +
////                sbn.getNotification().category + "\t" + sbn.getPackageName());
//    }
//
//    @Override
//    public void onListenerDisconnected() {
//        super.onListenerDisconnected();
//        String notificationListenerString = Settings.Secure.getString(this.getContentResolver(),"enabled_notification_listeners");
//        //Check notifications access permission
//        if (!(notificationListenerString == null || !notificationListenerString.contains(getPackageName())))
//        {
//            requestRebind(ComponentName.createRelative(this.getApplicationContext().getPackageName(), "ListenerNotificationTest"));
//        }
//    }
//
//    public class NotificationListenerRecetor extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String acao = intent.getAction();
//            if (acao == null) {
//                return;
//            }
//            if (Objects.equals(ACAO_ATIVIDADE_NOTIFICACOES, acao)) {
//                String chave = intent.getStringExtra(ACTIVITY_KEY);
//                if (chave == null) {
//                    return;
//                }
//                if (RECOLHER.equals(chave)) {
//                    if (!notificacoesGuardadas.isEmpty()) {
//                        for (List<String> not : notificacoesGuardadas) {
//                            Intent notificaoesAtividade = new Intent(ACAO_NOTIFICACOES_ATIVIDADE);
//                            notificaoesAtividade.putExtra(DIRETIVA, not.toArray(new String[0]));
//                            sendBroadcast(notificaoesAtividade);
//                        }
//                        notificacoesGuardadas.clear();
//                    }
//                }
//            }
//        }
//    }
}
