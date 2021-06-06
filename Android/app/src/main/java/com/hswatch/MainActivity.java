package com.hswatch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;
import com.hswatch.databinding.ActivityMainBinding;
import com.hswatch.fragments.ConfigDeviceActivity;

import static com.hswatch.Utils.HISTORY_SHARED_PREFERENCES;
import static com.hswatch.Utils.NAME;
import static com.hswatch.Utils.PERMISSOES;
import static com.hswatch.Utils.CHECKER;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "hswatch_activity_main";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

//        Iniciar Menu Lateral
        iniciarEcraPrinciapal(savedInstanceState);
    }

    private void iniciarEcraPrinciapal(Bundle savedInstanceState) {
        binding.navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.atividadePrincipal,
                binding.toolbar, R.string.navigation_abrir, R.string.navigation_fechar);
        binding.atividadePrincipal.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame,
                    new paginaPrincipal()).commit();
            binding.navigationView.setCheckedItem(R.id.navigation_view);

//        Permissoes
            if (!temPermissao(this)) {
                ActivityCompat.requestPermissions(this, PERMISSOES, 1);
            }
        }
    }

    public void showSite() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://hackerschool.io"));
        startActivity(browserIntent);
    }

    private boolean temPermissao(Context context) {
        if (context != null) {
            for (String permissao : PERMISSOES) {
                if (ContextCompat.checkSelfPermission(context, permissao) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0) {
            boolean ativouPermissoes = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    ativouPermissoes = false;
                    break;
                }
            }
            if (ativouPermissoes) {
                configurarNotifcacoes();
            }
        }
    }

    void configurarNotifcacoes() {
        String notificationListenerString = Settings.Secure.getString(this.getContentResolver(),"enabled_notification_listeners");
        //Check notifications access permission
        if (notificationListenerString == null || !notificationListenerString.contains(getPackageName()))
        {
            //The notification access has not acquired yet!
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.AVISO_NOT_LIST))
                    .setPositiveButton("Sim", (dialog, which) -> startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")))
                    .setNegativeButton("Não", (dialog, which) -> {
                        dialog.cancel();
                        finish();
                    }).create().show();
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.atividadePrincipal.isDrawerOpen(GravityCompat.START)) {
            binding.atividadePrincipal.closeDrawer(GravityCompat.START);
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
                startActivity(new Intent(getApplicationContext(), ConfigDeviceActivity.class));
                break;

//                Definições
            case R.id.def:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;
            case R.id.apagar:
                SharedPreferences sharedPreferences = getSharedPreferences(HISTORY_SHARED_PREFERENCES, MODE_PRIVATE);
                sharedPreferences.edit()
                        .putString(NAME, "Erro")
                        .putBoolean(CHECKER, false)
                        .apply();
                finishAffinity();

//                Notificações
            case R.id.notif:
                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        }
        binding.atividadePrincipal.closeDrawer(GravityCompat.START);
        return true;
    }

}
