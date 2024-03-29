package com.hswatch;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.hswatch.Utils.CHAMADAS;
import static com.hswatch.Utils.NOTIFICACOES;

public class paginaPrincipal extends Fragment {

    private final List<opcoesItem> opcoesItems = new ArrayList<>();
    private opcoesAdapter OpcoesAdapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        opcoesItems.clear();
        opcoesItems.add(new opcoesItem(
                R.drawable.ic_notificacoes_recebidas,
                getResources().getString(R.string.notifications_received_item)
        ));
        opcoesItems.add(new opcoesItem(
                R.drawable.ic_chamadas_recebidas,
                getResources().getString(R.string.calls_received_item)
        ));

        return inflater.inflate(R.layout.fragment_disp, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.recyclerView = view.findViewById(R.id.rec_frag);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        this.OpcoesAdapter = new opcoesAdapter(opcoesItems);
        this.OpcoesAdapter.setOnitemclicklistener(this::acaoItem);

        populateRecyclerView(PreferenceManager.getDefaultSharedPreferences(view.getContext())
                .getBoolean("debugging_mode", false));

        ((TextView) view.findViewById(R.id.txt_about)).setOnClickListener(
                (View.OnClickListener) view1 -> ((MainActivity) requireActivity()).showSite());
    }

    private void acaoItem(int position) {
        switch (position) {
            case 0:
                iniciarAtividade(NOTIFICACOES);
                break;
            case 1:
                iniciarAtividade(CHAMADAS);
                break;
//            case 0:
//                Toast.makeText(getContext(), "Ativar configurações da conexão - Tempo de refresh", Toast.LENGTH_LONG).show();
//                break;
//            case 1:
//                Toast.makeText(getContext(), "Ativar Alarme - configurações dos alarmes e mais", Toast.LENGTH_LONG).show();
//                break;
            default:
                Toast.makeText(getContext(), "Fora de alcance " + position, Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void populateRecyclerView(boolean debuggingMode) {
        if (debuggingMode) {
            this.recyclerView.setAdapter(this.OpcoesAdapter);
        } else {
            this.recyclerView.setAdapter(null);
        }
        this.recyclerView.invalidate();
    }

    private void iniciarAtividade(String chave) {
        switch (chave) {
            case NOTIFICACOES:
                startActivity(new Intent(getActivity(), NotActivity.class));
                break;
            case CHAMADAS:
                startActivity(new Intent(getActivity(), CallActivity.class));
                break;
        }
    }
}
