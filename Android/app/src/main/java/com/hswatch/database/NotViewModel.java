package com.hswatch.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class NotViewModel extends AndroidViewModel {

    private NotRepository notRepository;

    private LiveData<List<Notificacao>> allNot;

    public NotViewModel(@NonNull Application application) {
        super(application);
        notRepository = new NotRepository(application);
        allNot = notRepository.getAllNot();
    }

    public LiveData<List<Notificacao>> getAllNot() {
        return allNot;
    }

    public void inserir(final Notificacao notificacao) {
        notRepository.inserir(notificacao);
    }

    public void deleteNot(Notificacao notificacao) {
        notRepository.deleteNot(notificacao);
    }

    public void deleteNotList(List<Notificacao> notificacoes) {
        notRepository.deleteNotList(notificacoes);
    }

    public void deleteAll() {
        notRepository.deleteAll();
    }
}
