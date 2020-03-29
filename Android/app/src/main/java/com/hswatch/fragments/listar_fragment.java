package com.hswatch.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import com.hswatch.Servico;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

public class listar_fragment extends Fragment {

    public ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.listar_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.listarLista);

        Set<BluetoothDevice> setBT = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        List<String> nomes = new ArrayList<>();
        if (setBT.size() > 0){
            for (BluetoothDevice disp : setBT){
                nomes.add(disp.getName());
            }
            listView.setEnabled(true);
            ArrayAdapter<String> listaNomes = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_expandable_list_item_1, nomes);
            listView.setAdapter(listaNomes);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nome = (String) listView.getItemAtPosition(position);
                Toast.makeText(view.getContext(), "Tentar conexão no dispositivo " + nome,
                        Toast.LENGTH_LONG).show();

                Intent iniciarServico = new Intent(view.getContext(), Servico.class);
//                iniciarServico.putExtra(Constantes.TEXTOBTSERV, nome);
//                iniciarServico.putExtra(Constantes.TEXTONOTSERV, "Conectado a " + nome);

                ContextCompat.startForegroundService(view.getContext(), iniciarServico);

            /*    colocar intent que inicia serviço, mandando nome do dispositivo a qual se tentou ligar
                  e texto para por na notificação do serviço    */
            }
        });
    }
}
