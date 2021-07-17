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
import com.hswatch.refactor.ConfigurationFragment;

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
                ActivityCompat.requestPermissions(this, Utils.PERMISSOES, 1);
            }
        }
    }

    public void showSite() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://hackerschool.io"));
        startActivity(browserIntent);
    }

    private boolean temPermissao(Context context) {
        if (context != null) {
            for (String permissao : Utils.PERMISSOES) {
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
            ConfigurationFragment configurationFragment = (ConfigurationFragment) getSupportFragmentManager()
                .findFragmentByTag(Utils.CONFIGURATION_SETUP_KEY);
            if (configurationFragment != null) {
                if (configurationFragment.getChildFragmentManager().getBackStackEntryCount() > 0) {
                    configurationFragment.getChildFragmentManager().popBackStack();
                } else {
                    finishAffinity();
                }
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
//            Página principal
            case R.id.casa:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame,
                        new paginaPrincipal(), Utils.MAIN_FRAGMENT_KEY).commit();
                break;

//                Lista de dispositivos guardados (facil de emparelhar no futuro)
//            case R.id.disp:
//                getSupportFragmentManager().beginTransaction().replace(R.id.frame,
//                        new paginaPrincipal()).commit();
//                break;

//            Iniciar uma nova conexão
            case R.id.novo:
//                startActivity(new Intent(getApplicationContext(), ConfigDeviceActivity.class));
                getSupportFragmentManager().beginTransaction().replace(R.id.frame,
                        new ConfigurationFragment(), Utils.CONFIGURATION_SETUP_KEY).commit();
                break;

//                Definições
            case R.id.def:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;
            case R.id.apagar:
                SharedPreferences sharedPreferences = getSharedPreferences(Utils.HISTORY_SHARED_PREFERENCES, MODE_PRIVATE);
                sharedPreferences.edit()
                        .putString(Utils.NAME, "Erro")
                        .putBoolean(Utils.CHECKER, false)
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
