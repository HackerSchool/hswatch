package com.hswatch;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hswatch.bluetooth.Servico;
import com.hswatch.database.NotListAdapter;
import com.hswatch.database.NotViewModel;
import com.hswatch.database.Notificacao;
import com.hswatch.databinding.ActivityNotBinding;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.hswatch.Utils.ACAO_ATIVIDADE_NOTIFICACOES;
import static com.hswatch.Utils.ACAO_NOTIFICACOES_ATIVIDADE;
import static com.hswatch.Utils.ACTIVITY_KEY;
import static com.hswatch.Utils.DIRETIVA;
import static com.hswatch.Utils.RECOLHER;
import static com.hswatch.Utils.separador;

public class NotActivity extends AppCompatActivity {

    private static final String TAG = "NotActivity.TAG";

    private ActivityNotBinding binding;
    private NotListAdapter notListAdapter;
    private static NotViewModel notViewModel;
    private int delimitador = 0;
    public List<Notificacao> notificacaoList;
    public static boolean notAtividadeAtiva = false;
    private Recetor recetor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        binding.notToolbar.setTitle(R.string.NOT_TITULO);
        setSupportActionBar(binding.notToolbar);
        try {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        binding.notRecycler.setLayoutManager(new LinearLayoutManager(this));
        notListAdapter = new NotListAdapter(this);
        binding.notRecycler.setAdapter(notListAdapter);
        notViewModel = new ViewModelProvider(this).get(NotViewModel.class);
        notViewModel.getAllNot().observe(this, this::chunkData);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (notListAdapter.obterNotificacao(viewHolder.getAdapterPosition()) != null) {
                    notViewModel.deleteNot(notListAdapter.obterNotificacao(viewHolder.getAdapterPosition()));
                }
            }
        }).attachToRecyclerView(binding.notRecycler);

//        binding.notBtnApagar.setOnClickListener(view -> notViewModel.deleteAll());
        binding.notBtnDummy.setOnClickListener(view -> enviarDummyNot());
        binding.notFab.setOnClickListener(view -> binding.notScrollView.smoothScrollTo(0, 0));
        binding.notEditSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                notListAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.notBtnAdicionar.setOnClickListener(view -> chunkData());
    }

    private void enviarDummyNot() {
        byte[][] mensagemNotificacao = {
                "NOT".getBytes(),
                separador,
                "com.hswatch".getBytes(),
                separador,
                DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.UK).format(new Date().getTime())
                        .split(":")[0].getBytes(),
                separador,
                DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.UK).format(new Date().getTime())
                        .split(":")[1].getBytes(),
                separador,
                "HSWatch".getBytes(),
                separador,
                "This is a dummy notification.\nKeep hacking!".getBytes(),
                Utils.delimitador
        };
        Servico.enviarMensagensRelogio(mensagemNotificacao);
    }

    @SuppressLint("SetTextI18n")
    private void chunkData(List<Notificacao> notificacoes) {
        notificacaoList = new ArrayList<>(notificacoes);
        delimitador = 31;
        if (notificacoes.isEmpty())
            return;
        if (notificacoes.size() > delimitador)
            notListAdapter.setNotificacoes(notificacoes.subList(0, delimitador));
        else
            notListAdapter.setNotificacoes(notificacoes);

        binding.notBtnApagar.setText(getResources().getString(R.string.apagar) + " (" + notificacaoList.size() + ")");
        binding.notBtnExport.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TITLE, "Exported_Data.txt");

            startActivityForResult(intent, 5);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 5) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                String header = "id;nome;package_name;time_received;category;title;message\n";
                try {
                    OutputStream outputStream = getContentResolver().openOutputStream(uri);
                    outputStream.write(header.getBytes());
                    for (Notificacao notificacao : notificacaoList) {
                        outputStream.write((notificacao.toString() + "\n").getBytes());
                    }
                    outputStream.close();
                    Toast.makeText(this, "Finished it!", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error found: " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Stopped transferring!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void chunkData() {
        if (delimitador * 2 > notificacaoList.size()) {
            notListAdapter.adicionarNotificacoes(notificacaoList.subList(delimitador, notificacaoList.size()));
        } else {
            notListAdapter.adicionarNotificacoes(notificacaoList.subList(delimitador, delimitador * 2));
            delimitador += delimitador;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Iniciar broadcastReceiver
        iniciarBroadcastReceiver();

        // Recolher notificações registadas pelo listener
        Intent recolher = new Intent(ACAO_ATIVIDADE_NOTIFICACOES);
        recolher.putExtra(ACTIVITY_KEY, RECOLHER);
        sendBroadcast(recolher);

        // Indicar que a atividade está pronta
        notAtividadeAtiva = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        notAtividadeAtiva = false;
        unregisterReceiver(recetor);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void iniciarBroadcastReceiver() {
        recetor = new Recetor();

        IntentFilter intentFilter = new IntentFilter(ACAO_NOTIFICACOES_ATIVIDADE);
        registerReceiver(recetor, intentFilter);

    }

    public static void recetorSMS(List<String> smsRecebido) {
        notViewModel.inserir(new Notificacao(smsRecebido.get(0), smsRecebido.get(1),
                smsRecebido.get(2), smsRecebido.get(3), smsRecebido.get(4), smsRecebido.get(5)));
    }

    private static class Recetor extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String acao = intent.getAction();
            if (acao != null) {
                if (Objects.equals(acao, ACAO_NOTIFICACOES_ATIVIDADE)) {
                    String[] notificacao = intent
                            .getStringArrayExtra(DIRETIVA);
                    if (notificacao != null) {
                        notViewModel.inserir(new Notificacao(notificacao[0], notificacao[1], notificacao[2],
                                notificacao[3], notificacao[4], notificacao[5]));
                    }
                }
            }
        }
    }
}