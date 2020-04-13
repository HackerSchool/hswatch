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

    private static final String DEFINICOES = "definicoes_hswatch";
    private static final String DEFINICOES_UNIDADE_TEMPO = "definicoes_unidade_tempo";
    private static final String DEFINICOES_CIDADE = "definicoes_cidade";

    private RequestQueue requestQueue;

    private Map<String, String> semanaNumeroMap = new HashMap<>();

    private Map<String, Integer> idCidade = new HashMap<>();

    private long hora_inicial;
    private String cidade = "Lisbon";
    private Context context;

    public Profile (Context context, String nome) {
//        Iniciar contador
        hora_inicial = System.nanoTime();

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
        String url = "https://api.openweathermap.org/data/2.5/forecast?id=" + this.idCidade.get(this.cidade) + "&appid=c8e0a561f6bef99dddf373438831ed08";
        final List<String> mensagemClima = new ArrayList<>();
        mensagemClima.add(this.cidade);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("list");
                            for (int j = 0; j < jsonArray.length(); j = j + 8) {
                                JSONObject condicoes = jsonArray.getJSONObject(j).getJSONObject("main");
                                JSONObject clima = jsonArray.getJSONObject(j).getJSONArray("weather").getJSONObject(0);

                                mensagemClima.add(String.valueOf(conversorTempo(condicoes.getInt("temp"))));
                                mensagemClima.add(String.valueOf(condicoes.getInt("humidity")));
                                mensagemClima.add(tradutorTempo(clima.getString("main")));
                                mensagemClima.add(clima.getString("icon"));
                                mensagemClima.add(jsonArray.getJSONObject(j).getString("dt_txt"));
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
        String unidade = sharedPreferences.getString(DEFINICOES_UNIDADE_TEMPO, "Celsius");
//        unidade = (unidade != null) ? unidade : "Celsius";
        switch (unidade) {
            case "Celsius":
                return valor - 276;
            case "Fahrenheit":
                return (int) ((valor-276)*1.8 + 32);
            default:
                return valor;
        }
    }

    private String tradutorTempo(String tempo) {
        switch (tempo) {
            case "Rain":
                return "Chuva";
            case "Clear":
                return "Sol";
            case "Clouds":
                return "Nublado";
            default:
                return tempo;
        }
    }

    boolean passagem_de_hora(){
        long hora_passada = System.nanoTime();
//        6e10 = 1 minuto
        boolean verificador_da_passagem = hora_passada - this.hora_inicial > 6e10;
        if (verificador_da_passagem){
            this.hora_inicial = hora_passada;
        }
        return verificador_da_passagem;
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
