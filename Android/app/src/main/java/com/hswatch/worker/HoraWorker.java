package com.hswatch.worker;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.hswatch.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.hswatch.Constantes.delimitador;
import static com.hswatch.Constantes.separador;
import static com.hswatch.bluetooth.Servico.enviarMensagensRelogio;

public class HoraWorker extends Worker {

    private Map<String, String> semanaNumeroMap = new HashMap<>();

    public HoraWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

//        Criar Mapa de conversão de dias de semana para um numero
        String[] semanaArray = context.getResources().getStringArray(R.array.nomes_semana);
        for (int i = 1; i <= semanaArray.length; i++) {
            semanaNumeroMap.put(semanaArray[i - 1], String.valueOf(i));
        }
    }

    @SuppressLint("WorkerHasAPublicModifier")
    @NonNull
    @Override
    public Result doWork() {
        try{
            enviarMensagensRelogio(recetorTempoBytes());
//            return Result.success(new Data.Builder().putStringArray("its_time_to_stop", recetorTempo()).build());
            return Result.success();
        } catch (Throwable throwable){
            throwable.printStackTrace();
            return Result.failure();
        }
    }

    String[] recetorTempo() {
        String[] hora = DateFormat.getTimeInstance().format(new Date()).split(":");
        String[] data = DateFormat.getDateInstance().format(new Date()).split("/");
        return new String[]{
                hora[0], hora[1], hora[2],
                data[0], data[1], data[2],
                semanaNumeroMap.get(DateFormat.getDateInstance(DateFormat.FULL)
                        .format(new Date()).split(",")[0])
        };
    }

    private byte[][] recetorTempoBytes() {
        String[] hora = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.UK).format(new Date())
                .split(":");
        String[] data = DateFormat.getDateInstance(DateFormat.SHORT, Locale.UK).format(new Date())
                .split("/");
        return new byte[][]{
                "Tim".getBytes(), separador, hora[0].getBytes(), separador, hora[1].getBytes(),
                separador, hora[2].getBytes(), separador, data[0].getBytes(), separador,
                data[1].getBytes(), separador, data[2].getBytes(), separador,
                Objects.requireNonNull(semanaNumeroMap.get(DateFormat.getDateInstance(DateFormat.FULL)
                        .format(new Date()).split(",")[0])).getBytes(), delimitador

        };
    }


}
