package com.hswatch;

import android.Manifest;

import java.util.HashMap;
import java.util.UUID;

public class Constantes {
    //    Chaves dos Recetores
    public static final String DEFINICOES_HISTORIA = "historia_dispositivos_conectados";
    public static final String ATIVIDADE_CHAVE = "hswatch_atividade_chave";
    public static final String VERIFICADOR = "verificador_conexao";
    public static final String NOME = "historia_nome_dispositivo";
    //    public static final String ELEMENTO_SERVICO_NOT = "sinal_elemento_not";
    public static final String ACAO_NOTIFICACOES_SERVICO = "acao_notificacao_servico";
    public static final String ACAO_SERVICO_TEMPO_API = "acao_api_tempo_servico";
    public static final String ACAO_DEFINICOES_SERVICO = "acao_definicoes_servico";
    public static final String ACAO_ATIVIDADE_NOTIFICACOES = "acao_ativividade_notificacoes";
    public static final String ACAO_NOTIFICACOES_ATIVIDADE = "acao_notificacoes_atividade";
    public static final String RECOLHER = "recolher";
    public static final String DIRETIVA = "diretiva";

//    UUID
    public static final UUID uid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

//    Indicadores de mensagens
    public static final String INDICADOR_CLIMA = "WEA";
    public static final String INDICADOR_WHATS = "WHA";
    public static final String INDICADOR_FACEB = "FAC";
    public static final String INDICADOR_MESSE = "MES";
    public static final String INDICADOR_INSTA = "INS";
    public static final String INDICADOR_EMAIL = "EMA";
    public static final String INDICADOR_SMS = "SMS";
    public static final String INDICADOR_TEL = "TEL";
    public static final String INDICADOR_HSW = "HSW";
    public static HashMap<String, String> packagesNotFiltro = new HashMap<String, String>(){{
        put("com.whatsapp", INDICADOR_WHATS);
        put("com.instagram.android", INDICADOR_INSTA);
        put("com.facebook.orca", INDICADOR_MESSE);
        put("com.facebook.katana", INDICADOR_FACEB);
        put("com.google.android.gm", INDICADOR_EMAIL);
        put("sms", INDICADOR_SMS);
        put("com.hswatch", INDICADOR_HSW);
    }};

//    Chaves do protocolo
    public static final byte[] separador = {0x03};
    public static final byte[] delimitador = {0x00};

    //    Permissões
    public static final String[] PERMISSOES = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    //    Constantes internas
    public static final String NOTIFICACOES = "notificações";
    public static final String CHAMADAS = "chamadas";


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
