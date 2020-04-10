package com.hswatch;

import android.graphics.drawable.AnimationDrawable;
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
                Toast.makeText(getContext(), "Cliqou no " + position, Toast.LENGTH_LONG).show();
//                começar atividade
            }
        });
    }

}
