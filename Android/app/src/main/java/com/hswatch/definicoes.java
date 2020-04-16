package com.hswatch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.hswatch.bluetooth.Profile;
import com.hswatch.bluetooth.Servico;

import java.util.ArrayList;
import java.util.List;

public class definicoes extends AppCompatActivity {

    public static final String TAG = "hswatch.atividade.definicoes";

    public static final String ACAO_DEFINICOES_SERVICO = "hswatch.definicoes.servico";
    public static final String DIRETIVA = "hswatch.definicoes.servico.diretiva";

    List<String> cidadesArray;

    Recetor recetor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definicoes);
        Toolbar toolbar = findViewById(R.id.def_toolbar);
        toolbar.setTitle(R.string.MENU_DEFINICOES);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (NullPointerException e) {
            Log.e(TAG, "Erro na toolbar", e);
        }

        recetor = new Recetor();
        cidadesArray = new ArrayList<>();

        List<String> unidadesTemperatura = new ArrayList<>();
        unidadesTemperatura.add("ºC");
        unidadesTemperatura.add("ºF");
        unidadesTemperatura.add("K");

        preencherSpinner(Profile.DEFINICOES_UNIDADE_TEMPO, "ºC", unidadesTemperatura,
                (AppCompatSpinner) findViewById(R.id.indicador_unidade),
                R.layout.spinner_texto_layout, R.layout.spinner_lista_layout);

        IntentFilter intentFilter = new IntentFilter(Servico.ACAO_SERVICO_DEFINICOES);
        registerReceiver(recetor, intentFilter);
        sendBroadcast(new Intent(ACAO_DEFINICOES_SERVICO).putExtra(DIRETIVA, "recolher"));

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(recetor);
    }

    private void preencherSpinner(final String chaveMemoria, String valorDefault, List<String> dadosSpinner,
                                  AppCompatSpinner spinner, @Nullable Integer recursoItem,
                                  @Nullable  Integer recursoLayout) {
        final SharedPreferences sharedPreferences = getSharedPreferences(Profile.DEFINICOES, MODE_PRIVATE);
        String dadoEscolhido = sharedPreferences.getString(chaveMemoria, valorDefault);
        if (dadoEscolhido == null) {
            List<String> spinnerArray = new ArrayList<>();
            spinnerArray.add("NPE");
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                    (recursoItem != null) ? recursoItem : android.R.layout.simple_spinner_item, spinnerArray);
            arrayAdapter.setDropDownViewResource((recursoLayout != null) ? recursoLayout : R.layout.spinner_lista_layout);
            spinner.setAdapter(arrayAdapter);
            return;
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                (recursoItem != null) ? recursoItem : android.R.layout.simple_list_item_1, dadosSpinner);
        arrayAdapter.setDropDownViewResource((recursoLayout != null) ? recursoLayout : R.layout.spinner_lista_layout);
        spinner.setAdapter(arrayAdapter);
        for (int i = 0; i < dadosSpinner.size(); i++) {
            if (dadoEscolhido.equals(spinner.getItemAtPosition(i).toString())) {
                spinner.setSelection(i);
                break;
            }
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sharedPreferences.edit().putString(chaveMemoria,
                        parent.getItemAtPosition(position).toString()).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    class Recetor extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String acao = intent.getAction();
            Log.v(TAG, "Recebido do Serviço: " + intent.getStringExtra(DIRETIVA));
            if (acao == null) {
                return;
            }
            if (acao.equals(Servico.ACAO_SERVICO_DEFINICOES)) {
                if (intent.getStringExtra(DIRETIVA).equals("limpar") && cidadesArray.size() > 0) {
                    cidadesArray.clear();
                } else if (intent.getStringExtra(DIRETIVA).equals("ativar")) {
                    preencherSpinner(Profile.DEFINICOES_CIDADE, "Lisboa", cidadesArray,
                            (AppCompatSpinner) findViewById(R.id.indicador_cidade),
                            R.layout.spinner_texto_layout, R.layout.spinner_lista_layout);
                    sendBroadcast(new Intent(ACAO_DEFINICOES_SERVICO).putExtra(DIRETIVA, "alterar"));
                } else if(intent.getStringExtra(DIRETIVA).equals("Erro")) {
                    cidadesArray.add("Erro");
                } else {
                    cidadesArray.add(intent.getStringExtra(DIRETIVA));
                }
            }
        }

    }

}
