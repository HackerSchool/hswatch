package com.hswatch.bluetooth;

import android.content.Context;

import androidx.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hswatch.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Profile {

    private RequestQueue requestQueue;

    private Map<String, String> semanaNumeroMap = new HashMap<>();

    private Context context;

    public interface VolleyCallBack {
        void returnoSucedido(List<String> respostaLimpa);
    }


    public Profile (Context context, String nome) {
//        Iniciar contador
//        hora_inicial_tempo = System.nanoTime();

        this.context = context;
        this.requestQueue = Volley.newRequestQueue(this.context);

//        Criar Mapa de conversão de dias de semana para um numero
        String[] semanaArray = context.getResources().getStringArray(R.array.nomes_semana);
        for (int i = 1; i <= semanaArray.length; i++) {
            semanaNumeroMap.put(semanaArray[i - 1], String.valueOf(i));
        }
    }

    public void jsonParserTempo(final VolleyCallBack callBack) {
        String cidade = PreferenceManager.getDefaultSharedPreferences(context).getString("cidades", "Lisbon");
        String unidade = PreferenceManager.getDefaultSharedPreferences(context).getString("unidades", "M");
        if (cidade == null || unidade == null) {
            return;
        }
        final List<String> mensagemClima = new ArrayList<>();
        mensagemClima.add((Objects.equals("Lisbon", cidade) ? "Lisboa" : cidade));
//        final byte[][] mensagem = new byte[53][];
//        mensagem[0] = INDICADOR_CLIMA.getBytes();
//        mensagem[1] = separador;
//        mensagem[2] = (Objects.equals("Lisbon", cidade) ? "Lisboa" : cidade).getBytes();
//        final int[] index = {3};
        String url = "https://api.weatherbit.io/v2.0/forecast/daily?city=" + cidade +
                "&country_full=Portugal&units=" + unidade + "&key=e2cd4478289c4b5ab5ac602203922b80&days=6";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("data");
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject condicoes = jsonArray.getJSONObject(j);
                            String icon = condicoes.getJSONObject("weather").getString("code");

//                            mensagem[index[0]] = separador;
//                            mensagem[index[0] + 1] = icon.getBytes();
//                            mensagem[index[0] + 2] = separador;
//                            mensagem[index[0] + 3] = String.valueOf(condicoes.getDouble("max_temp")).getBytes();
//                            mensagem[index[0] + 4] = separador;
//                            mensagem[index[0] + 5] = String.valueOf(condicoes.getDouble("min_temp")).getBytes();
//                            mensagem[index[0] + 6] = separador;
//                            mensagem[index[0] + 7] = String.valueOf(condicoes.getInt("pop")).getBytes();
//                            index[0] = index[0] + 8;

                            mensagemClima.add(icon);
                            mensagemClima.add(String.valueOf(condicoes.getDouble("max_temp")));
                            mensagemClima.add(String.valueOf(condicoes.getDouble("min_temp")));
                            mensagemClima.add(String.valueOf(condicoes.getInt("pop")));
                        }
//                        mensagem[index[0]] = delimitador;
                        callBack.returnoSucedido(mensagemClima);
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
        this.requestQueue.add(request);
    }

//    boolean passagem_de_hora(){
//        long hora_passada = System.nanoTime();
//
//        if (!obter_API) {
//            boolean verificador_da_passagem_clima = hora_passada - this.hora_inicial_clima > 3*60*6e10;
//            if (verificador_da_passagem_clima) {
//                obter_API = true;
//            }
//        }
//
////        6e10 = 1 minuto
//        boolean verificador_da_passagem_hora = hora_passada - this.hora_inicial_tempo > 6e10;
//        if (verificador_da_passagem_hora)
//            this.hora_inicial_tempo = hora_passada;
//
//        return verificador_da_passagem_hora;
//    }
    String[] recetorTempo() {
        String[] hora = DateFormat.getTimeInstance().format(new Date()).split(":");
        String[] data = DateFormat.getDateInstance().format(new Date()).split("/");
        return new String[]{
                hora[0], hora[1], hora[2],
                data[0], data[1], data[2],
                semanaNumeroMap.get(DateFormat.getDateInstance(DateFormat.FULL)
                        .format(new Date()).split(",")[0])
        };
    }

}
