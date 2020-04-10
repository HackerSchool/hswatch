package com.hswatch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.hswatch.bluetooth.sem_bt;
import com.hswatch.fragments.atividade_config;

public class atividade_splash extends AppCompatActivity {

    public static final String TAG = "hswatch.ativ.splash";

    public static final String HISTORIA_PREFS = "historia_dispositivos_conectados";
    public static final String VERIFICADOR = "verificador_conexao";
    public static final String NOME = "historia_nome_dispositivo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atividade_splash);
        startActivity(new Intent(this, MainActivity.class));
//        verificacao_Inicial();
//        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), getResources().getInteger(R.integer.ATIVAR_BT));
    }

    private void verificacao_Inicial() {
        BluetoothAdapter radioBT = BluetoothAdapter.getDefaultAdapter();
        if (radioBT == null){
            Toast.makeText(this, "Bluetooth Indisponível!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == getResources().getInteger(R.integer.ATIVAR_BT) && resultCode == RESULT_CANCELED){
//            Mostrar atividade de não funcionamento
            startActivity(new Intent(this, sem_bt.class));
            Toast.makeText(getApplicationContext(), "O HSWatch necessita de o bluetooth ligado para poder operar.", Toast.LENGTH_LONG).show();
        } else if (requestCode == getResources().getInteger(R.integer.ATIVAR_BT) && resultCode == RESULT_OK) {
            verificarHistoriaDispositivos();
        }
    }

    private void verificarHistoriaDispositivos() {
        SharedPreferences sharedPreferences = getSharedPreferences(HISTORIA_PREFS, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(VERIFICADOR, false)) {
            Toast.makeText(getApplicationContext(), "Está conectado ao dispositivo: " +
                    sharedPreferences.getString(NOME, "Erro"), Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, atividade_config.class));
        }
    }
}
