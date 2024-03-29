package com.hswatch.database;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hswatch.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.hswatch.Utils.packagesNotFilter;

//TODO(documentar)
public class NotListAdapter extends RecyclerView.Adapter<NotListAdapter.NotViewHolder>
        implements Filterable {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    @Override
    public Filter getFilter() {
        return notFilter;
    }

    private final Filter notFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            if (notificacoes == null || notificacaosTotal == null) {
                return null;
            }

            List<Notificacao> notFiltrada = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                notFiltrada.addAll(notificacaosTotal);
            } else {
                String pesquisa = charSequence.toString().toLowerCase().trim();
                for (String key : packagesNotFilter.keySet()) {
                    if (key.contains(pesquisa)) {
                        for (Notificacao notificacao : notificacaosTotal) {
                            if (Objects.equals(notificacao.getPackageName(), packagesNotFilter.get(key))) {
                                notFiltrada.add(notificacao);
                            }
                        }
                        break;
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = notFiltrada;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            if (notificacoes != null) {
                notificacoes.clear();
                notificacoes.addAll((List) filterResults.values);
                notifyDataSetChanged();
            }
        }
    };

    public static class NotViewHolder extends RecyclerView.ViewHolder {

        final TextView headerTxt, titleTxt, textTxt, packageTxt, categoryTxt, dateTxt; 

        public NotViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTxt = itemView.findViewById(R.id.item_not_header);
            titleTxt = itemView.findViewById(R.id.item_not_title);
            textTxt = itemView.findViewById(R.id.item_not_text);
            packageTxt = itemView.findViewById(R.id.item_not_package);
            categoryTxt = itemView.findViewById(R.id.item_not_category);
            dateTxt = itemView.findViewById(R.id.item_not_date);
        }
    }

    private final LayoutInflater layoutInflater;
    private List<Notificacao> notificacoes;
    private List<Notificacao> notificacaosTotal;

    public NotListAdapter (Context context) { layoutInflater = LayoutInflater.from(context); }

    @NonNull
    @Override
    public NotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.not_disp, parent, false);
        return new NotViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotViewHolder holder, int position) {
        if (notificacoes != null) {
            Notificacao current = notificacoes.get(position);
            holder.headerTxt.setText(current.getNome());
            holder.titleTxt.setText(current.getTitle());
            holder.textTxt.setText(current.getMessage());
            holder.packageTxt.setText(current.getPackageName());
            holder.categoryTxt.setText(current.getCategory());
            holder.dateTxt.setText(current.getTime_received());
        }
    }

    public void setNotificacoes(List<Notificacao> notificacoes){
        if (!notificacoes.isEmpty()) {
            this.notificacoes = notificacoes;
        } else {
            Notificacao notificacao = new Notificacao("Não há notificações!", "", "","",
                    "", "");
            this.notificacoes = new ArrayList<>();
            this.notificacoes.add(notificacao);
        }
        this.notificacaosTotal = new ArrayList<>(this.notificacoes);
        notifyDataSetChanged();
    }

    public void adicionarNotificacoes(List<Notificacao> notificacoes) {
        if (this.notificacoes.get(0).nome.equals("Não há notificações!")) {
            this.notificacoes = notificacoes;
        } else {
            this.notificacoes.addAll(notificacoes);
        }
        notifyDataSetChanged();
    }

    public Notificacao obterNotificacao(int posicao) {
        if (notificacoes != null) {
            return notificacoes.get(posicao);
        } else {
            return null;
        }
    }

    @Override
    public int getItemCount() {
        if (notificacoes != null)
            return notificacoes.size();
        return 0;
    }
}
