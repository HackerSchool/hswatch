package com.hswatch.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ChamadaViewModel extends AndroidViewModel {

    private ChamadaRepository chamadaRepository;

    private LiveData<List<Chamada>> allCha;

    public ChamadaViewModel(@NonNull Application application) {
        super(application);
        chamadaRepository = new ChamadaRepository(application);
        allCha = chamadaRepository.getAllCha();
    }

    public LiveData<List<Chamada>> getAllCha() {
        return allCha;
    }

    public void inserir(final Chamada chamada) {
        chamadaRepository.inserir(chamada);
    }

    public void deleteChamada(final Chamada chamada) {
        chamadaRepository.deleteChamada(chamada);
    }

    public void deleteAll() {
        chamadaRepository.deleteAll();
    }
}
