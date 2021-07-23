package com.hswatch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

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

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                binding.atividadePrincipal,
                binding.toolbar,
                R.string.navigation_abrir,
                R.string.navigation_fechar
        );

        binding.atividadePrincipal.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            checkMainActivityMode();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean debuggingMode = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getBoolean("debugging_mode", false);

        binding.navigationView.getMenu().findItem(R.id.apagar).setVisible(
                debuggingMode
        );

        binding.navigationView.getMenu().findItem(R.id.notif).setVisible(
                debuggingMode
        );

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(Utils.MAIN_FRAGMENT_KEY);

        if (fragment != null) {
            if (fragment.isVisible()) {
                ((paginaPrincipal) fragment).populateRecyclerView(debuggingMode);
            }
        }
    }

    private void checkMainActivityMode() {
        int mode = getIntent().getIntExtra(Utils.MAIN_ACTIVITY_MODE, Utils.MAIN_ACTIVITY_CONNECTION);

        if (mode == Utils.MAIN_ACTIVITY_FIRST_START) {
            startConfiguration(true);
            binding.toolbar.setVisibility(View.GONE);
        } else if (mode == Utils.MAIN_ACTIVITY_NEEDS_CONNECTION) {
            startConfiguration(false);
            binding.toolbar.setVisibility(View.VISIBLE);
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame,
                    new paginaPrincipal(), Utils.MAIN_FRAGMENT_KEY)
                    .commit();

            binding.toolbar.setVisibility(View.VISIBLE);

            if (gotPermissions(this)) {
                ActivityCompat.requestPermissions(this, Utils.PERMISSOES, 1);
            }
        }

    }

    private void startConfiguration(boolean initialSetup) {
        ConfigurationFragment configurationFragment = new ConfigurationFragment();
        Bundle bundle = new Bundle();

        bundle.putBoolean(Utils.CONFIGURATION_MODE, initialSetup);
        configurationFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame,
                configurationFragment, Utils.CONFIGURATION_SETUP_KEY)
                .commit();
    }

    public void showSite() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://hackerschool.io"));
        startActivity(browserIntent);
    }

    private boolean gotPermissions(Context context) {
        if (context != null) {
            for (String permissao : Utils.PERMISSOES) {
                if (ContextCompat.checkSelfPermission(context, permissao) !=
                        PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
            }
        }
        return false;
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
                    .setPositiveButton("Sim", (dialog, which) ->
                            startActivity(new Intent(Settings
                                    .ACTION_NOTIFICATION_LISTENER_SETTINGS)))
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
//            MainFragment
            case R.id.casa:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame,
                        new paginaPrincipal(), Utils.MAIN_FRAGMENT_KEY).commit();
                break;

//            Setup
            case R.id.novo:
                startConfiguration(false);
                break;

            case R.id.setup:
                startConfiguration(true);
                break;

//            Settings
            case R.id.def:
                startActivity(new Intent(getApplicationContext(),
                        SettingsActivity.class));
                break;

//            Debug
            case R.id.apagar:
                SharedPreferences sharedPreferences = getSharedPreferences(
                        Utils.HISTORY_SHARED_PREFERENCES,
                        MODE_PRIVATE
                );
                sharedPreferences.edit().clear().apply();
                finishAffinity();
            case R.id.notif:
                startActivity(new Intent(Settings
                        .ACTION_NOTIFICATION_LISTENER_SETTINGS));
        }
        binding.atividadePrincipal.closeDrawer(GravityCompat.START);
        return true;
    }

    public void verifyNotificationsSetup() {
        if (gotPermissions(this)) {
            ActivityCompat.requestPermissions(this, Utils.PERMISSOES, 1);
        }

        if (this.binding.toolbar.getVisibility() == View.GONE) {
            this.binding.toolbar.setVisibility(View.VISIBLE);
        }
    }
}
