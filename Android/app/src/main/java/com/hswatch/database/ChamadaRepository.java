package com.hswatch.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ChamadaRepository {

    private ChamadaDao chamadaDAO;
    private LiveData<List<Chamada>> allCha;

    ChamadaRepository(Application application) {
        NotRoomDatabase notRoomDatabase = NotRoomDatabase.getDatabase(application);
        chamadaDAO = notRoomDatabase.chamadaDAO();
        allCha = chamadaDAO.getChamadas();
    }

    public LiveData<List<Chamada>> getAllCha() {
        return allCha;
    }

    public void inserir(final Chamada chamada) {
        NotRoomDatabase.databaseWriteExecutor.execute(() -> {
            chamadaDAO.inserir(chamada);
        });
    }

    public void deleteChamada(Chamada chamada) {
        NotRoomDatabase.databaseWriteExecutor.execute(() -> {
            chamadaDAO.deleteChamada(chamada);
        });
    }

    public void deleteAll() {
        NotRoomDatabase.databaseWriteExecutor.execute(() -> {
            chamadaDAO.deleteAll();
        });
    }
}
