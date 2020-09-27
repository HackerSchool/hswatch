package com.hswatch.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chamada_table")
public class Chamada {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nome_chamada, numero_chamada, estado_chamada, hora_chamada;

    public Chamada(String nome_chamada, String numero_chamada, String estado_chamada, String hora_chamada) {
        this.nome_chamada = nome_chamada;
        this.numero_chamada = numero_chamada;
        this.estado_chamada = estado_chamada;
        this.hora_chamada = hora_chamada;
    }

    public String getNome_chamada() {
        return nome_chamada;
    }

    public String getNumero_chamada() {
        return numero_chamada;
    }

    public String getEstado_chamada() {
        return estado_chamada;
    }

    public String getHora_chamada() {
        return hora_chamada;
    }
}
