package com.hswatch.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Notificacao.class, Chamada.class}, version = 2, exportSchema = false)
public abstract class NotRoomDatabase extends RoomDatabase {

    static final Migration MIGRATION_1_2 = new Migration(1, 2){
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `chamada_table` (`id` INTEGER PRIMARY KEY " +
                    "AUTOINCREMENT NOT NULL, `nome_chamada` TEXT, `numero_chamada` TEXT, `estado_chamada` " +
                    "TEXT, `hora_chamada` TEXT)");
//            "CREATE TABLE IF NOT EXISTS `User` (`id` INTEGER, PRIMARY KEY(`id`))"
        }
    };

    public abstract NotDao notDao();
    public abstract ChamadaDao chamadaDAO();

    private static volatile NotRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static NotRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (NotRoomDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        NotRoomDatabase.class, "not_database")
                        .fallbackToDestructiveMigration()
                        .addMigrations(MIGRATION_1_2)
                        .build();
            }
        }
        return INSTANCE;
    }

}
