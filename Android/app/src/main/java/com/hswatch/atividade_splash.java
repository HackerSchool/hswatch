package com.hswatch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
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
        verificacao_Inicial();
    }

    private void verificacao_Inicial() {
        BluetoothAdapter radioBT = BluetoothAdapter.getDefaultAdapter();
        if (radioBT == null){
            Toast.makeText(this, "Bluetooth Indisponível!", Toast.LENGTH_LONG).show();
            finish();
        }
        String notificationListenerString = Settings.Secure.getString(this.getContentResolver(),"enabled_notification_listeners");
        //Check notifications access permission
        if (notificationListenerString == null || !notificationListenerString.contains(getPackageName()))
        {
            //The notification access has not acquired yet!
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("O HSWatch necessita do acesso às notificações para o seu funcionamento e de efeitos de demonstragem.\nDeseja ativar a leitura das notificações?")
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), getResources().getInteger(R.integer.ATIVAR_BT));
                        }
                    })
                    .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finish();
                        }
                    });
            builder.create().show();
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
