package com.hswatch;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String HISTORIA_PREFS = "historia_dispositivos_conectados";
    public static final String VERIFICADOR = "verificador_conexao";
    public static final String NOME = "historia_nome_dispositivo";

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verificacao_Inicial();
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        verificarHistoriaDispositivos();
        iniciarEcraPrinciapal();
    }

    private void verificarHistoriaDispositivos() {
        SharedPreferences sharedPreferences = getSharedPreferences(HISTORIA_PREFS, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(VERIFICADOR, false)) {
            Toast.makeText(getApplicationContext(), "Está conectado ao dispositivo: " +
                    sharedPreferences.getString(NOME, "Erro"), Toast.LENGTH_LONG).show();
        } else {
            startActivity(new Intent(MainActivity.this, atividade_config.class));
        }
    }

    private void iniciarEcraPrinciapal() {
        drawerLayout = findViewById(R.id.atividade_principal);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_abrir, R.string.navigation_fechar);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().beginTransaction().replace(R.id.frame, new paginaPrincipal()).commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void verificacao_Inicial() {
        BluetoothAdapter radioBT = BluetoothAdapter.getDefaultAdapter();
        if (radioBT == null){
            Toast.makeText(this, "Bluetooth Indisponível!", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
