package com.hswatch.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NotDao {

    @Insert
    void inserir(Notificacao notificacao);

    @Delete
    void deleteNot(Notificacao notificacao);

    @Delete
    void deleteNotList(List<Notificacao> notificacoes);

    @Query("DELETE FROM not_table")
    void deleteAll();

    @Query("SELECT * from not_table ORDER BY id DESC")
    LiveData<List<Notificacao>> getNotifications();
}
