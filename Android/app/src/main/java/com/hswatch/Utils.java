package com.hswatch;

import android.Manifest;
import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Utils {


    /**
     * Keys to use on SharedPreferences
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
    public static final String INSTAGRAM_PACKAGENAME = "com.instagram.android";
    public static final Map<String, String> packagesNotIndicator = new HashMap<String, String>() {{
        put(WHATSAPP_PACKAGENAME, INDICADOR_WHATS);
        put(INSTAGRAM_PACKAGENAME, INDICADOR_INSTA);
        put("com.facebook.orca", INDICADOR_MESSE);
        put("com.facebook.katana", INDICADOR_FACEB);
        put("com.google.android.gm", INDICADOR_EMAIL);
        put("sms", INDICADOR_SMS);
        put("com.hswatch", INDICADOR_HSW);
    }};
    public static final Map<String, String> packagesNotFilter = new HashMap<String, String>() {{
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
     * An array of permissions to request to the user. Some of these permissions need to be on the
     * Manifest to be called.
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
    public static final String SETUP_APP_BACKGROUNDMODE = "SETUP_APP_BACKGROUNDMODE";
    public static final String FIRST_START = "FIRST_START";
    public static final String MAIN_ACTIVITY_MODE = "MAIN_ACTIVITY_MODE";
    public static final int MAIN_ACTIVITY_FIRST_START = 0;
    public static final int MAIN_ACTIVITY_CONNECTION = 1;
    public static final int MAIN_ACTIVITY_NEEDS_CONNECTION = 2;
    public static final String CONFIGURATION_MODE = "CONFIGURATION_MODE";

    @NonNull
    public static String[] getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        int[] date = Arrays.stream(new int[]{
                // HH                 mm               SS
                Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND,
                // DD                  MM              AAAA
                Calendar.DAY_OF_MONTH, Calendar.MONTH, Calendar.YEAR,
                // Weekday number
                Calendar.DAY_OF_WEEK
            })
            .map(calendar::get)
            .toArray();

        // Month is counted from zero
        date[4] += 1;
        return Arrays.stream(date).mapToObj(Integer::toString).toArray(String[]::new);
    }

    /**
     * Get the Weather API key to get the weather status up to 6 days.
     * It should be added to URL and not to get the actual key
     *
     * @return a string to be added onto the end of the url
     */
    @NonNull
    public static String getKey(String apiKey) {
        return "&key=" + apiKey + "&days=6";
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
    public static final String NOT_INDICATOR = "NOT";


    /**
     * Setup Fragment Keys
     */
    public static final String MAIN_FRAGMENT_KEY = "MAIN_FRAGMENT";
    public static final String CONFIGURATION_SETUP_KEY = "CONFIGURATION_SETUP_FRAGMENT";
    public static final String INITIAL_APP_STATE_TAG_FRAGMENT = "INITIAL_APP_STATE_TAG_FRAGMENT";
    public static final String FUNCTIONALITY_APP_STATE_TAG_FRAGMENT = "FUNCTIONALITY_APP_STATE_TAG_FRAGMENT";
    public static final String PAIR_APP_STATE_TAG_FRAGMENT = "PAIR_APP_STATE_TAG_FRAGMENT";
    public static final String BACKGROUND_SERVICE_APP_STATE_TAG_FRAGMENT = "BACKGROUND_SERVICE_APP_STATE_TAG_FRAGMENT";
    public static final String SETUP_TAG_FRAGMENT = "SETUP_TAG_FRAGMENT";
    public static final String INITIAL_STATE_TAG_FRAGMENT = "INITIAL_STATE_TAG_FRAGMENT";
    public static final String FINISHING_TAG_FRAGMENT = "FINISHING_TAG_FRAGMENT";
    public static final String SETUP_APP_TITLE = "SETUP_APP_TITLE";
    public static final String SETUP_APP_CONTENT = "SETUP_APP_CONTENT";
    public static final String SETUP_APP_CONTENT_DESCRIPTION = "SETUP_APP_CONTENT_DESCRIPTION";
    public static final String SETUP_APP_IMAGE_RESOURCE = "SETUP_APP_IMAGE_RESOURCE";
    public static final String SETUP_APP_BUTTON_TEXT = "SETUP_APP_BUTTON_TEXT";
    public static final String SETUP_APP_STATUS = "SETUP_APP_STATUS";
    public static final int NEXT_FROM_APP_START = 0;
    public static final int NEXT_FROM_APP_FUNC = 1;
    public static final int NEXT_FROM_APP_PAIR = 2;
    public static final int NEXT_FROM_APP_BACKGROUND_SERVICE = 3;
    public static final int INITIAL_STATE = 4;
    public static final int NEXT_FROM_START = 5;
    public static final int NEXT_FROM_LIST = 6;
    public static final int NEXT_FROM_FINISH = 7;
    public static final int BACKGROUND_APP = 0;
    public static final int BACKGROUND_CONNECTION = 1;
    public static volatile boolean tryConnecting = false;
    public static volatile boolean connectionSucceeded = false;


    public static void testAPI(String apiKeyTest, Context context, UtilsTestConnectionCallback callback) {
        String url = "https://api.weatherbit.io/v2.0/forecast/daily?city=Lisbon&key=" + apiKeyTest;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> callback.statusResponse(true),
                error -> callback.statusResponse(false)
        );
        Volley.newRequestQueue(context).add(request);
    }

    public interface UtilsTestConnectionCallback {
        void statusResponse(boolean responseSucceed);
    }

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
