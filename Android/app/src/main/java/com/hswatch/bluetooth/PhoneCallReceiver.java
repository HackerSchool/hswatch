package com.hswatch.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.hswatch.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.hswatch.CallActivity.chamadaAtividadeAtiva;
import static com.hswatch.CallActivity.recetorChamada;

//TODO(refactor this)
public class PhoneCallReceiver extends BroadcastReceiver {

    public static final String TAG = "hswatch_phone_listener";

    public static int estadoAnterior = TelephonyManager.CALL_STATE_IDLE;
    public static boolean estaReceber;

    public static List<List<String>> chamadasRegistadas = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED, intent.getAction())) {
            obterNumeroEstado(context, intent);
        }
    }

    private void obterNumeroEstado(Context context, Intent intent) {
        String number = Objects.requireNonNull(intent.getExtras())
                .getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
        String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
        if (number != null) {
            int estado = 10;
            if (Objects.equals(stateStr, TelephonyManager.EXTRA_STATE_IDLE)) {
                estado = TelephonyManager.CALL_STATE_IDLE;
            } else if (Objects.equals(stateStr, TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                estado = TelephonyManager.CALL_STATE_OFFHOOK;
            }
            else if(Objects.equals(stateStr, TelephonyManager.EXTRA_STATE_RINGING)){
                estado = TelephonyManager.CALL_STATE_RINGING;
            }

            verificadorEstado(estado, number, obterNomePorNumero(context, number), context);
        }
    }

    public static String obterNomePorNumero(Context context, String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));

        String[] projecao = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        Cursor cursor = context.getContentResolver().query(uri, projecao, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
            cursor.close();
        }
        return number;
    }

    public void verificadorEstado(int estado, String number, String nome, Context context) {
        if (estadoAnterior != estado) {
            /*
            * Estado 0: Não há chamada - IDLE
            * Estado 1: Receber a chamada - RINGING
            * Estado 2: Chamada a decorrer - OFFHOOK
            */
            switch (estado) {
                case TelephonyManager.CALL_STATE_RINGING:
                    estaReceber = true;
                    String horaRecebida = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.UK)
                            .format(new Date().getTime());
                    if (chamadaAtividadeAtiva) {
                        recetorChamada(nome, number, "Receber", horaRecebida);
                    } else {
                        List<String> chamadaRecebida = new ArrayList<String>(){{
                            add(nome);
                            add(number);
                            add("Receber");
                            add(horaRecebida);
                        }};
                        chamadasRegistadas.add(chamadaRecebida);
                    }
                    MainServico.sendCalls(number, nome, horaRecebida,
                            context.getResources().getString(R.string.RECEIVED));
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if (estadoAnterior == TelephonyManager.CALL_STATE_RINGING) {
                        String horaPerdida = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.UK).format(new Date().getTime());
                        MainServico.sendCalls(number, nome, horaPerdida,
                                context.getResources().getString(R.string.LOST));
                        if (chamadaAtividadeAtiva) {
                            recetorChamada(nome, number, "Perdida", horaPerdida);
                        } else {
                            List<String> chamadaRecebida = new ArrayList<String>(){{
                                add(nome);
                                add(number);
                                add("Perdida");
                                add(horaPerdida);
                            }};
                            chamadasRegistadas.add(chamadaRecebida);
                        }
                    }
                    break;
                default:break;
            }
            estadoAnterior = estado;
        }
    }
}