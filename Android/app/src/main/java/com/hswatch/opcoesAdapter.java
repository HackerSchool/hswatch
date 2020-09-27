package com.hswatch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class opcoesAdapter extends RecyclerView.Adapter<opcoesAdapter.opcoesViewHolder> {

    private ArrayList<opcoesItem> opcoesItems;

    private onItemClickListener onitemclicklistener;

    public interface onItemClickListener{
        void onItemClick(int position);
    }

    public void setOnitemclicklistener(onItemClickListener onItemClickListener) {
        onitemclicklistener = onItemClickListener;
    }

    @NonNull
    @Override
    public opcoesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.opcao_disp, parent, false);
        return new opcoesViewHolder(v, onitemclicklistener);
    }

    @Override
    public void onBindViewHolder(@NonNull opcoesViewHolder holder, int position) {
        opcoesItem currentItem = opcoesItems.get(position);
        holder.marcaTexto.setText(currentItem.opTexto);
        holder.marcaImagem.setImageResource(currentItem.opImagem);
    }

    @Override
    public int getItemCount() {
        return opcoesItems.size();
    }

    public opcoesAdapter (ArrayList<opcoesItem> opcoesItemArrayList) {opcoesItems=opcoesItemArrayList;}

    public static class opcoesViewHolder extends RecyclerView.ViewHolder {
        private ImageView marcaImagem;
        private TextView marcaTexto;
        public opcoesViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);
            marcaImagem = itemView.findViewById(R.id.op_simb);
            marcaTexto = itemView.findViewById(R.id.op_txt);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

}
