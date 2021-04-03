package com.hswatch.fragments;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.hswatch.R;
import com.hswatch.databinding.ActivityConfigBinding;

import java.util.ArrayList;
import java.util.List;

import static com.hswatch.Utils.HISTORY_SHARED_PREFERENCES;
import static com.hswatch.Utils.NAME;
import static com.hswatch.Utils.CHECKER;

public class ConfigDeviceActivity extends AppCompatActivity {

    public static final String TAG = "hswatch.config";

    private String nome;

    private viewPagerAdapter viewPagerAdapter;
    private ActivityConfigBinding binding;

    private configRecetor recetor = new configRecetor();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfigBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        List<Fragment> listaFragments = new ArrayList<>();
        listaFragments.add(new apresentador_fragment());
        listaFragments.add(new listar_fragment());
        listaFragments.add(new finalizador_fragment());

        binding.viewerPage.setOnTouchListener((v, event) -> true);
        viewPagerAdapter = new viewPagerAdapter(getSupportFragmentManager(), listaFragments);
        binding.viewerPage.setAdapter(viewPagerAdapter);


        IntentFilter intentFilter = new IntentFilter(getResources().getString(R.string.FINALIZAR_FRAG));
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(getResources().getString(R.string.LISTAR_FRAG));
        registerReceiver(recetor, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(recetor);
    }

    public void seguir_fragment () {
        binding.viewerPage.setCurrentItem(binding.viewerPage.getCurrentItem() + 1);
    }

    public void anterior_fragment() {
        binding.viewerPage.setCurrentItem(binding.viewerPage.getCurrentItem() - 1);
    }

    public void guardarDispositivo() {
//        Utilizar o sharedpreferences para guardar o nome e dizer que está em conexão
        SharedPreferences sharedPreferences = getSharedPreferences(HISTORY_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(NAME, nome);
        editor.putBoolean(CHECKER, true);

        editor.apply();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (binding.viewerPage.getCurrentItem() == 0) {
                finish();
            } else {
                anterior_fragment();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void listaBluetooth(boolean mostrar) {
        if (mostrar)
            ((listar_fragment) viewPagerAdapter.getItem(1)).listarDispositivosPareados();
        else
            ((listar_fragment) viewPagerAdapter.getItem(1)).limparLista();
    }

    public class configRecetor extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String acao = intent.getAction();
            Log.v(TAG, "Recebido acao " + acao);
            if (acao != null) {
                if (acao.equals(getResources().getString(R.string.LISTAR_FRAG)) &&
                        intent.getBooleanExtra(getResources().getString(R.string.SINAL_VERDE), false)) {
                    try {
                        seguir_fragment();
                        nome = intent.getStringExtra(getResources().getString(R.string.NOME));
                    } catch (NullPointerException e) {
                        Log.e(TAG, "Não foi possível receber o nome do dispositivo", e);
                    }
//                } else if (acao.equals(getResources().getString(R.string.LISTAR_FRAG)) &&
//                        !intent.getBooleanExtra(getResources().getString(R.string.SINAL_VERDE), false)) {
//                    verificador = false;
                } else if (acao.equals(BluetoothAdapter.ACTION_STATE_CHANGED) && intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR) ==
                        BluetoothAdapter.STATE_ON) {
                    listaBluetooth(true);
                } else if (acao.equals(BluetoothAdapter.ACTION_STATE_CHANGED) && intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR) ==
                        BluetoothAdapter.STATE_OFF) {
                    listaBluetooth(false);
                }
            }
        }
    }
}
