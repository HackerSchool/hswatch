package com.hswatch.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class NotRepository {

    private NotDao notDao;
    private LiveData<List<Notificacao>> allNot;

    NotRepository(Application application) {
        NotRoomDatabase notRoomDatabase = NotRoomDatabase.getDatabase(application);
        notDao = notRoomDatabase.notDao();
        allNot = notDao.getNotifications();
    }

    public LiveData<List<Notificacao>> getAllNot() {
        return allNot;
    }

    public void inserir(final Notificacao notificacao) {
        NotRoomDatabase.databaseWriteExecutor.execute(() -> {
            notDao.inserir(notificacao);
        });
    }

    public void deleteNot(Notificacao notificacao) {
        NotRoomDatabase.databaseWriteExecutor.execute(() -> {
            notDao.deleteNot(notificacao);
        });
    }

    public void deleteNotList(List<Notificacao> notificacoes) {
        NotRoomDatabase.databaseWriteExecutor.execute(() -> {
            notDao.deleteNotList(notificacoes);
        });
    }

    public void deleteAll() {
        NotRoomDatabase.databaseWriteExecutor.execute(() -> {
            notDao.deleteAll();
        });
    }
}
