package com.hswatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.hswatch.fragments.atividade_config;

import static com.hswatch.atividade_splash.HISTORIA_PREFS;
import static com.hswatch.fragments.atividade_config.NOME;
import static com.hswatch.fragments.atividade_config.VERIFICADOR;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        Iniciar Menu Lateral
        iniciarEcraPrinciapal(savedInstanceState);
    }

    void configurarNotifcacoes() {
        String notificationListenerString = Settings.Secure.getString(this.getContentResolver(),"enabled_notification_listeners");
        //Check notifications access permission
        if (notificationListenerString == null || !notificationListenerString.contains(getPackageName()))
        {
            //The notification access has not acquired yet!
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.AVISO_NOT_LIST))
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                        }
                    })
                    .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finish();
                        }
                    }).create().show();
        }
    }

    private void iniciarEcraPrinciapal(Bundle savedInstanceState) {
        drawerLayout = findViewById(R.id.atividade_principal);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_abrir, R.string.navigation_fechar);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame,
                    new paginaPrincipal()).commit();
            navigationView.setCheckedItem(R.id.navigation_view);

//        Permissoes
            configurarNotifcacoes();
        }
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
//            Página principal
            case R.id.casa:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame,
                        new paginaPrincipal()).commit();
                break;

//                Lista de dispositivos guardados (facil de emparelhar no futuro)
//            case R.id.disp:
//                getSupportFragmentManager().beginTransaction().replace(R.id.frame,
//                        new paginaPrincipal()).commit();
//                break;

//            Iniciar uma nova conexão
            case R.id.novo:
                startActivity(new Intent(getApplicationContext(), atividade_config.class));
                break;

//                Definições
            case R.id.def:
                startActivity(new Intent(getApplicationContext(), definicoes.class));
                break;
            case R.id.apagar:
                SharedPreferences sharedPreferences = getSharedPreferences(HISTORIA_PREFS, MODE_PRIVATE);
                sharedPreferences.edit()
                        .putString(NOME, "Erro")
                        .putBoolean(VERIFICADOR, false)
                        .apply();
                finishAffinity();

//                Notificações
            case R.id.notif:
                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
