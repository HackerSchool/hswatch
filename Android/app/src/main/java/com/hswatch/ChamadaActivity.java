package com.hswatch;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hswatch.database.Chamada;
import com.hswatch.database.ChamadaListAdapter;
import com.hswatch.database.ChamadaViewModel;
import com.hswatch.databinding.ActivityChamadaBinding;

import java.util.List;
import java.util.Objects;

import static com.hswatch.bluetooth.PhoneCallReceiver.chamadasRegistadas;

public class ChamadaActivity extends AppCompatActivity {

    private ActivityChamadaBinding binding;
    private ChamadaListAdapter chamadaListAdapter;
    private static ChamadaViewModel chamadaViewModel;

    public static boolean chamadaAtividadeAtiva = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChamadaBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        binding.chaToolbar.setTitle(R.string.CHA_TITULO);
        setSupportActionBar(binding.chaToolbar);
        try {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

//        binding.chaRecycler.setHasFixedSize(true);
        binding.chaRecycler.setLayoutManager(new LinearLayoutManager(this));
        chamadaListAdapter = new ChamadaListAdapter(this);
        binding.chaRecycler.setAdapter(chamadaListAdapter);
        chamadaViewModel = new ViewModelProvider(this).get(ChamadaViewModel.class);
        if (chamadasRegistadas != null) {
            if (!chamadasRegistadas.isEmpty()) {
                for (List<String> chamada : chamadasRegistadas) {
                    recetorChamada(chamada.get(0), chamada.get(1), chamada.get(2), chamada.get(3));
                }
                chamadasRegistadas.clear();
            }
        }
        chamadaViewModel.getAllCha().observe(this, chamadas -> chamadaListAdapter.setChamadas(chamadas));
        chamadaViewModel.getAllCha().observe(this, notificacaoList -> chamadaListAdapter.setChamadas(notificacaoList));
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (chamadaListAdapter.obterChamada(viewHolder.getAdapterPosition()) != null) {
                    chamadaViewModel.deleteChamada(chamadaListAdapter.obterChamada(viewHolder.getAdapterPosition()));
                }
            }
        }).attachToRecyclerView(binding.chaRecycler);

        binding.chaBtnApagar.setOnClickListener(view -> chamadaViewModel.deleteAll());
        binding.chaFab.setOnClickListener(view -> binding.chaScrollView.smoothScrollTo(0, 0));
    }

    @Override
    protected void onStart() {
        super.onStart();
        chamadaAtividadeAtiva = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        chamadaAtividadeAtiva = false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static void recetorChamada(String nome, String numero, String estado, String hora) {
        chamadaViewModel.inserir(new Chamada(nome, numero, estado, hora));
    }


}