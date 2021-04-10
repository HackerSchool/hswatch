package com.hswatch.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.hswatch.R;
import com.hswatch.refactor.Servico;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class listar_fragment extends Fragment {

    public static final String TAG = "hswatch.frag.listar";

//    Objetos UI
    private ListView listView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.listar_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.listarLista);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            String nome = (String) listView.getItemAtPosition(position);
            Toast.makeText(view1.getContext(), "Tentar conexão no dispositivo " + nome,
                    Toast.LENGTH_LONG).show();

            Intent iniciarServico = new Intent(view1.getContext(), Servico.class);
            iniciarServico.putExtra(getResources().getString(R.string.ServicoDisp), nome);

            ContextCompat.startForegroundService(view1.getContext(), iniciarServico);
        });
    }

    public void limparLista() {
        listView.setAdapter(null);
    }

    public void listarDispositivosPareados() {
        Set<BluetoothDevice> setBT = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        List<String> nomes = new ArrayList<>();
        if (setBT.size() > 0){
            for (BluetoothDevice disp : setBT){
                nomes.add(disp.getName());
            }
            ArrayAdapter<String> listaNomes = new ArrayAdapter<>(requireContext(),
                    R.layout.lista_emparelhados_layout, nomes);
            listView.setAdapter(listaNomes);
        }
    }
}
