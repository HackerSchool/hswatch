package com.hswatch.bluetooth;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hswatch.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class Profile {

    public static final String DEFINICOES = "definicoes_hswatch";
    public static final String DEFINICOES_UNIDADE_TEMPO = "definicoes_unidade_tempo";
    public static final String DEFINICOES_CIDADE = "definicoes_cidade";

    private RequestQueue requestQueue;

    private Map<String, String> semanaNumeroMap = new HashMap<>();

    private Map<String, Integer> idCidade = new HashMap<>();

    private long hora_inicial_tempo, hora_inicial_clima;
    private String cidade = "Lisbon";
    private Context context;
    private boolean obter_API = true;

    public Profile (Context context, String nome) {
//        Iniciar contador
        hora_inicial_tempo = System.nanoTime();

        this.context = context;
        this.requestQueue = Volley.newRequestQueue(this.context);

        criarMapCidade();

//        Criar Mapa de conversão de dias de semana para um numero
        String[] semanaArray = context.getResources().getStringArray(R.array.nomes_semana);
        for (int i = 1; i <= semanaArray.length; i++) {
            semanaNumeroMap.put(semanaArray[i - 1], String.valueOf(i));
        }
    }

    public List<String> jsonParserTempo() {
        if (!obter_API) { return null; }
        String url = "http://api.weatherbit.io/v2.0/forecast/daily?city="+ this.idCidade.get(this.cidade) +"&key=e2cd4478289c4b5ab5ac602203922b80&days=6";
        final List<String> mensagemClima = new ArrayList<>();
        mensagemClima.add(this.cidade);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int j = 0; j < jsonArray.length(); j ++) {
                                JSONObject condicoes = jsonArray.getJSONObject(j);
                                JSONObject icon = condicoes.getJSONArray("weather").getJSONObject(0);

                                mensagemClima.add(icon.toString());
                                mensagemClima.add(String.valueOf(conversorTempo(condicoes.getInt("max_temp"))));
                                mensagemClima.add(String.valueOf(condicoes.getInt("min_temp")));
                                mensagemClima.add(String.valueOf(condicoes.getInt("pop")));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        this.requestQueue.add(request);
        obter_API = false;
        hora_inicial_clima = System.nanoTime();
        return mensagemClima;
    }

    public void alterarCidade() {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(DEFINICOES, MODE_PRIVATE);
        this.cidade = sharedPreferences.getString(DEFINICOES_CIDADE, "Lisboa");
    }

    public Map<String, Integer> getIdCidade() {
        return idCidade;
    }

    private int conversorTempo(int valor) {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(DEFINICOES, MODE_PRIVATE);
        String unidade = sharedPreferences.getString(DEFINICOES_UNIDADE_TEMPO, "ºC");
//        unidade = (unidade != null) ? unidade : "Celsius";
        switch (unidade) {
            case "ºC":
                return valor - 276;
            case "ºF":
                return (int) ((valor-276)*1.8 + 32);
            default:
                return valor;
        }
    }

    boolean passagem_de_hora(){
        long hora_passada = System.nanoTime();

        if (!obter_API) {
            boolean verificador_da_passagem_clima = hora_passada - this.hora_inicial_clima > 3*60*6e10;
            if (verificador_da_passagem_clima) {
                obter_API = true;
            }
        }

//        6e10 = 1 minuto
        boolean verificador_da_passagem_hora = hora_passada - this.hora_inicial_tempo > 6e10;
        if (verificador_da_passagem_hora)
            this.hora_inicial_tempo = hora_passada;

        return verificador_da_passagem_hora;
    }

    private void criarMapCidade() {
        JSONObject jsonObject = obterJSON();
        if (jsonObject != null) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("portugal");
                for (int i = jsonArray.length()-1; i > 0; i--) {
                    String cidadeNome = jsonArray.getJSONObject(i).getString("name");
                    int cidadeID = jsonArray.getJSONObject(i).getInt("id");
                    idCidade.put(cidadeNome, cidadeID);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONObject obterJSON() {
        try {
            InputStream inputStream = this.context.getAssets().open("JSONPT_limpo.json");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            return new JSONObject(json);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    
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
