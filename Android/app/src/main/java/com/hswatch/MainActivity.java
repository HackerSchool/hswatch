package com.hswatch;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.navigation.NavigationView;
import com.hswatch.databinding.ActivityMainBinding;
import com.hswatch.dialog.ConfigDialog;
import com.hswatch.refactor.ConfigurationFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ActivityCompat.OnRequestPermissionsResultCallback {

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
        } else if (mode == Utils.MAIN_ACTIVITY_NEEDS_CONNECTION) {
            startConfiguration(false);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame,
                    new paginaPrincipal(), Utils.MAIN_FRAGMENT_KEY)
                    .commit();

            checkPermissionsDialog();

            activateToolbar();

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

    private boolean gotPermissionsAccepted(Context context) {
        if (context != null) {
            for (String permissao : Utils.PERMISSOES) {
                if (ContextCompat.checkSelfPermission(context, permissao) !=
                        PackageManager.PERMISSION_GRANTED) {
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
                configureDialog();
            }
        }
    }

    /**
     * A function which can create and show a {@link ConfigDialog} object to request the API KEY to
     * the user
     */
    private void configureDialog() {
        ConfigDialog configDialog = new ConfigDialog(
                getString(R.string.API_KEY_TITLE),
                getString(R.string.API_KEY_CONTENT),
                new ConfigDialog.ConfigOptions() {
                    @Override
                    public void positiveButton(String key, ConfigDialog configDialog) {
                        Utils.testAPI(key, getApplicationContext(), responseSucceed -> {
                            if (responseSucceed) {
                                PreferenceManager.getDefaultSharedPreferences(
                                        getApplicationContext()
                                ).edit().putString(getString(R.string.KEY_API_PREFERENCES), key).apply();
                                Toast.makeText(
                                        getApplicationContext(),
                                        getString(R.string.toast_API_key_accepting),
                                        Toast.LENGTH_SHORT
                                ).show();
                                configDialog.dismiss();
                            } else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        getString(R.string.toast_API_key_error),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        });
                    }

                    @Override
                    public void negativeButton(ConfigDialog configDialog) {
                        Toast.makeText(
                                getApplicationContext(),
                                getString(R.string.toast_API_key_missing),
                                Toast.LENGTH_SHORT
                        ).show();
                        configDialog.dismiss();
                    }
                });
        configDialog.show(getSupportFragmentManager(), "config_dialog");
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
                PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();
                finishAffinity();
            case R.id.notif:
                startActivity(new Intent(Settings
                        .ACTION_NOTIFICATION_LISTENER_SETTINGS));
        }
        binding.atividadePrincipal.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * This function shows a sequence of dialogs within ask the user for permissions and/or other
     * values which are necessary for the user to use the app at its full potential.
     * <p>
     * If needed, it can grant access for more permissions, as long as those permissions are in the
     * {@link Utils#PERMISSOES}
     */
    public void checkPermissionsDialog() {
        String api_key = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.KEY_API_PREFERENCES), "");

        // Checks for the access on the permissions
        if (!gotPermissionsAccepted(this)) {
            ActivityCompat.requestPermissions(this, Utils.PERMISSOES, 1);
        } else {
            // In case that they were accepted, check if the API Key was insert on the app's
            // SharedPreferences
            if (api_key != null && api_key.isEmpty()) {
                configureDialog();
            }
        }
    }

    /**
     * Activates the toolbar, whether it's GONE or VISIBLE
     */
    public void activateToolbar() {
        this.binding.toolbar.setVisibility(View.VISIBLE);
    }

    public void connectionOn() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit().putBoolean("connection", true).apply();
    }
}
