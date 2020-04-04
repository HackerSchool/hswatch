package com.hswatch.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.hswatch.R;
import com.hswatch.bluetooth.Servico;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class listar_fragment extends Fragment {

//    TAG
    private static final String TAG = "hswatch.fragment.listar";

//    Objetos UI
    private ListView listView;

//    BroadcastReceiver
    private Recetor recetor;

    private boolean verificador = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.listar_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.listarLista);

        recetor = new Recetor();

        listarDispositivosPareados();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nome = (String) listView.getItemAtPosition(position);
                Toast.makeText(view.getContext(), "Tentar conexão no dispositivo " + nome,
                        Toast.LENGTH_LONG).show();

                Intent iniciarServico = new Intent(view.getContext(), Servico.class);
                iniciarServico.putExtra(getResources().getString(R.string.ServicoDisp), nome);

                ContextCompat.startForegroundService(view.getContext(), iniciarServico);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(getResources().getString(R.string.LISTAR_FRAG));
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        Objects.requireNonNull(getActivity()).registerReceiver(recetor, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        Objects.requireNonNull(getActivity()).unregisterReceiver(recetor);

    }

    private void listarDispositivosPareados() {
        Set<BluetoothDevice> setBT = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        List<String> nomes = new ArrayList<>();
        if (setBT.size() > 0){
            for (BluetoothDevice disp : setBT){
                nomes.add(disp.getName());
            }
            listView.setEnabled(true);
            ArrayAdapter<String> listaNomes = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_expandable_list_item_1, nomes);
            listView.setAdapter(listaNomes);
        }
    }

    public class Recetor extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String acao = intent.getAction();
            if (acao != null) {
                Log.v(TAG, "Sinal Verde!");
                if (acao.equals(getResources().getString(R.string.LISTAR_FRAG)) &&
                        intent.getBooleanExtra(getResources().getString(R.string.SINAL_VERDE), false) && verificador) {
                    try {
                        ((atividade_config) Objects.requireNonNull(getActivity())).seguir_fragment();
                    } catch (NullPointerException e) {
                        Log.e(TAG, "Não foi possível fazer a transição", e);
                    }
                } else if (acao.equals(getResources().getString(R.string.LISTAR_FRAG)) &&
                        !intent.getBooleanExtra(getResources().getString(R.string.SINAL_VERDE), false)) {
                    verificador = false;
                }
            }
        }

    }
}