package com.hswatch.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "not_table")
public class Notificacao {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;

    @NonNull
    public String nome;

    public String package_name;

    @NonNull
    public String time_received;

    public String category;

    public String title;

    public String message;

    public Notificacao(@NonNull String nome, String package_name, @NonNull String time_received,
                       String category, String title, String message) {
        this.nome = nome;
        this.package_name = (package_name != null) ? package_name : " - ";
        this.category = category;
        this.title = (title != null) ? title : " - ";
        this.message = (message != null) ? message : " - ";
        this.time_received = time_received;
    }

    @NonNull
    public String getNome() {
        return nome;
    }

    public String getPackageName() {
        return package_name;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    @NonNull
    public String getTime_received() {
        return time_received;
    }


}
