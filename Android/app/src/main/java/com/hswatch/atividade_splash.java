package com.hswatch;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hswatch.bluetooth.sem_bt;
import com.hswatch.fragments.atividade_config;

import static com.hswatch.Constantes.DEFINICOES_HISTORIA;
import static com.hswatch.Constantes.NOME;
import static com.hswatch.Constantes.VERIFICADOR;

public class atividade_splash extends AppCompatActivity {

    public static final String TAG = "hswatch.ativ.splash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        startActivity(new Intent(this, MainActivity.class));
        finish();

//        if (ativoServico) {
//            startActivity(new Intent(this, MainActivity.class));
//            finish();
//        } else {
//            verificacao_Inicial();
//            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), getResources().getInteger(R.integer.ATIVAR_BT));
//        }
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
            startActivity(new Intent(this, MainActivity.class));
            finish();
            verificarHistoriaDispositivos();
        }
    }

    private void verificarHistoriaDispositivos() {
        SharedPreferences sharedPreferences = getSharedPreferences(DEFINICOES_HISTORIA, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(VERIFICADOR, false)) {
            Toast.makeText(getApplicationContext(), "Está conectado ao dispositivo: " +
                    sharedPreferences.getString(NOME, "Erro"), Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, atividade_config.class));
            finish();
        }
    }
}
