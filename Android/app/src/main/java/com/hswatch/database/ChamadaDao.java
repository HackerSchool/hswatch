package com.hswatch.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChamadaDao {

    @Insert
    void inserir(Chamada chamada);

    @Delete
    void deleteChamada(Chamada chamada);

    @Query("DELETE FROM chamada_table")
    void deleteAll();

    @Query("SELECT * FROM chamada_table ORDER BY id DESC")
    LiveData<List<Chamada>> getChamadas();

}
