package com.hswatch;

import android.Manifest;
import android.content.Context;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Utils {


    /**
     *  Keys to use on SharedPreferences
      */
    public static final String HISTORY_SHARED_PREFERENCES = "historia_dispositivos_conectados";
    public static final String CHECKER = "verificador_conexao";
    public static final String NAME = "historia_nome_dispositivo";

    /**
     * Keys to use on BroadCastReceivers
     */
    public static final String ACTIVITY_KEY = "hswatch_atividade_chave";
    public static final String ACAO_SERVICO_TEMPO_API = "acao_api_tempo_servico";
    public static final String ACAO_DEFINICOES_SERVICO = "acao_definicoes_servico";
    public static final String ACAO_ATIVIDADE_NOTIFICACOES = "acao_ativividade_notificacoes";
    public static final String ACAO_NOTIFICACOES_ATIVIDADE = "acao_notificacoes_atividade";
    public static final String RECOLHER = "recolher";
    public static final String DIRETIVA = "diretiva";
    public static final String ACAO_NOTIFICACOES_SERVICO = "acao_notificacao_servico";
    //    public static final String ELEMENTO_SERVICO_NOT = "sinal_elemento_not";

    /**
     * Service
     */
    public static final String BT_DEVICE_NAME = "device_name";
    public static final UUID uid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final int REQUEST_CODE_PENDING_INTENT = 2;
    public static final int FOREGROUND_ID = 4;
    public static final int NOTIFICATION_SERVICE_ID = 5;

    /**
     * Messages indicators
     */
    public static final String WEATHER_INDICATOR = "WEA";
    public static final String INDICADOR_WHATS = "WHA";
    public static final String INDICADOR_FACEB = "FAC";
    public static final String INDICADOR_MESSE = "MES";
    public static final String INDICADOR_INSTA = "INS";
    public static final String INDICADOR_EMAIL = "EMA";
    public static final String INDICADOR_SMS = "SMS";
    public static final String INDICADOR_TEL = "TEL";
    public static final String INDICADOR_HSW = "HSW";
    public static final String WHATSAPP_PACKAGENAME = "com.whatsapp";
    public static final Map<String, String> packagesNotIndicator = new HashMap<String, String>(){{
        put(WHATSAPP_PACKAGENAME, INDICADOR_WHATS);
        put("com.instagram.android", INDICADOR_INSTA);
        put("com.facebook.orca", INDICADOR_MESSE);
        put("com.facebook.katana", INDICADOR_FACEB);
        put("com.google.android.gm", INDICADOR_EMAIL);
        put("sms", INDICADOR_SMS);
        put("com.hswatch", INDICADOR_HSW);
    }};
    public static final Map<String, String> packagesNotFilter = new HashMap<String, String>(){{
        put("whatsapp", "com.whatsapp");
        put("instagram", "com.instagram.android");
        put("messenger", "com.facebook.orca");
        put("facebook", "com.facebook.katana");
    }};
    public static final String WHATSAPP_WEB = "WhatsApp Web";
    public static final String WHATSAPP_NAME = "WhatsApp";

    /**
     * Protocol's keys
      */
    public static final byte[] separador = {0x03};
    public static final byte[] delimitador = {0x00};

    /**
     * Permissions
     * Add here the permission needed and then, on the MainActivity, this will make an Dialog appear
     */
    public static final String[] PERMISSOES = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET
    };

    /**
     * Internal Utils
     */
    public static final String NOTIFICACOES = "notificações";
    public static final String CHAMADAS = "chamadas";

    /**
     * Key's for Activity's Response
     */
    public static final int BT_REQUEST = 1;

    @NonNull
    public static Map<String, String> getWeekArray(@NonNull Context context) {
        String[] weekArray = context.getResources()
                .getStringArray(R.array.nomes_semana);
        HashMap<String, String> returnMap = new HashMap<>();
        for (int i = 1; i <= weekArray.length; i++) {
            returnMap.put(weekArray[i - 1], String.valueOf(i));
        }
        return returnMap;
    }

    @NonNull
    public static String[] getCurrentTime (Map<String, String> weekMap) {
        String[] hora = DateFormat.getTimeInstance().format(new Date()).split(":");
        String[] data = DateFormat.getDateInstance().format(new Date()).split("/");
        return new String[]{
                // HH       mm      SS
                hora[0], hora[1], hora[2],
                // DD       MM      AAAA
                data[0], data[1], data[2],
                // Week number
                weekMap.get(DateFormat.getDateInstance(DateFormat.FULL)
                        .format(new Date()).split(",")[0])
        };
    }

    /**
     * Get the Weather API key to get the weather status up to 6 days.
     * It should be added to URL and not to get the actual key
     *
     * @return a string to be added onto the end of the url
     */
    public static String getKey() {
        return "&key=e2cd4478289c4b5ab5ac602203922b80&days=6";
    }

    /**
     * Weather API Constants
     */
    public static final String WEATHER_DATA = "data";
    public static final String WEATHER_ICON = "weather";
    public static final String WEATHER_ICON_CODE = "code";
    public static final String WEATHER_MAX_TEMP = "max_temp";
    public static final String WEATHER_MIN_TEMP = "min_temp";
    public static final String WEATHER_POP = "pop";

    /**
     * ThreadConnected class's keys
     */
    public static final String TAG_HOURS = "tag_hours";
    public static final String TIME_INDICATOR = "TIM";
    public static final String NOT_INDICATOR = "Not";

// Notas
//    private void terPackagesNames() {
//        PackageManager pm = getPackageManager();
//        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
//        for (ApplicationInfo app : apps) {
//            if(pm.getLaunchIntentForPackage(app.packageName) != null) {
//                // apps with launcher intent
//                if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
//                    // updated system apps
//
//                } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
//                    // system apps
//
//                } else {
//                    // user installed apps
//                    Log.v(TAG, app.name + "\t\t\t" + app.packageName + "\t\t\t" + app.category);
//                }
//            }
//        }
//    }
//  NotViewModel notViewModel = ViewModelProvider(getContext()).get(NotViewModel.class);
}
