package com.hswatch.database;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hswatch.R;

import java.util.List;

public class ChamadaListAdapter extends RecyclerView.Adapter<ChamadaListAdapter.ChamadaViewHolder> {
    public static class ChamadaViewHolder extends RecyclerView.ViewHolder {

        TextView nomeTxt, numeroTxt, estadoTxt,  horaTxt;

        public ChamadaViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeTxt = itemView.findViewById(R.id.item_chamada_nome);
            numeroTxt = itemView.findViewById(R.id.item_chamada_numero);
            estadoTxt = itemView.findViewById(R.id.item_chamada_estado);
            horaTxt = itemView.findViewById(R.id.item_chamada_hora);
        }

    }
    private final LayoutInflater layoutInflater;

    private List<Chamada> chamadas;
    public ChamadaListAdapter (Context context) { layoutInflater = LayoutInflater.from(context); }

    @NonNull
    @Override
    public ChamadaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.chamada_disp, parent, false);
        return new ChamadaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChamadaViewHolder holder, int position) {
        if (chamadas != null) {
            Chamada current = chamadas.get(position);
            holder.nomeTxt.setText(current.getNome_chamada());
            holder.numeroTxt.setText(current.getNumero_chamada());
            holder.estadoTxt.setText(current.getEstado_chamada());
            holder.horaTxt.setText(current.getHora_chamada());
        } else {
            holder.nomeTxt.setText("Tio Zé");
            holder.numeroTxt.setText("912345678");
            holder.estadoTxt.setText("Já deveria ter chamado");
            holder.horaTxt.setText("E já deveria ser tempo");
        }
    }

    @Override
    public int getItemCount() {
        if (chamadas != null) {
            return chamadas.size();
        }
        return 0;
    }

    public void setChamadas(List<Chamada> chamadas) {
        this.chamadas = chamadas;
        notifyDataSetChanged();
    }

    public Chamada obterChamada(int posicao) {
        if (chamadas != null) {
            return chamadas.get(posicao);
        } else {
            return null;
        }
    }
}
