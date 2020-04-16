package com.hswatch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class paginaPrincipal extends Fragment {

    private ArrayList<opcoesItem> opcoesItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        opcoesItems.add(new opcoesItem(R.drawable.ic_bluetooth_black_24dp, "Configurações de conexão"));
        opcoesItems.add(new opcoesItem(R.drawable.ic_access_alarm_black_24dp, "Alarme"));
        opcoesItems.add(new opcoesItem(R.drawable.ic_notifications_black_24dp, "Notificações"));
        return inflater.inflate(R.layout.fragment_disp, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rec_frag);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        opcoesAdapter OpcoesAdapter = new opcoesAdapter(opcoesItems);
        recyclerView.setAdapter(OpcoesAdapter);
        OpcoesAdapter.setOnitemclicklistener(new opcoesAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                acaoItem(position);
//                começar atividade
            }
        });
    }

    private void acaoItem(int position) {
        switch (position) {
            case 0:
                Toast.makeText(getContext(), "Ativar configurações da conexão - Tempo de refresh", Toast.LENGTH_LONG).show();
                break;
            case 1:
                Toast.makeText(getContext(), "Ativar Alarme - configurações dos alarmes e mais", Toast.LENGTH_LONG).show();
                break;
            case 2:
                Toast.makeText(getContext(), "Ativar Notificações - Verificar para quais aplicações o relógio mostra e história do que foi mandado", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(getContext(), "Fora de alcance" + position, Toast.LENGTH_LONG).show();
                break;
        }
    }

}
