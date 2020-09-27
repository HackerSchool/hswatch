package com.hswatch.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.hswatch.NotActivity.notAtividadeAtiva;
import static com.hswatch.NotActivity.recetorSMS;
import static com.hswatch.bluetooth.NotificationListener.NotificacaoRecebida;
import static com.hswatch.bluetooth.NotificationListener.notificacoesGuardadas;
import static com.hswatch.bluetooth.PhoneCallReceiver.obterNomePorNumero;

public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION, intent.getAction())) {
            receberSMS(context, intent);
        }
    }

    private void receberSMS(Context context, Intent intent) {
        for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
            String nome = obterNomePorNumero(context, smsMessage.getDisplayOriginatingAddress());
            String conteudo = smsMessage.getDisplayMessageBody();
            guardarNot(nome, conteudo);
            NotificacaoRecebida(nome, conteudo, " - ", "sms");
        }
    }

    // Função para os SMS's
    public void guardarNot(String smsNome, String smsConteudo) {
//        infoNot = { nome, packageName, time_received, category, title, text }
        List<String> notif = new ArrayList<String>(){{
            add("SMS");
            add(" - ");
            add(DateFormat.getDateInstance(DateFormat.SHORT, Locale.UK).format(new Date()) + " " +
                    DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.UK).format(new Date().getTime()));
            add(" - ");
            add(smsNome);
            add(smsConteudo);
        }};

        if (notAtividadeAtiva) {
            recetorSMS(notif);
        } else {
            notificacoesGuardadas.add(notif);
        }
    }
}
