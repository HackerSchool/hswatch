package com.hswatch.bluetooth;

import android.content.Context;

import com.hswatch.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.core.content.res.TypedArrayUtils;

public class Profile {

    private Map<String, String> semanaNumeroMap = new HashMap<>();
    private long hora_inicial;

    public Profile (Context context, String nome) {
//        Iniciar contador
        hora_inicial = System.nanoTime();

//        Criar Mapa de conversão de dias de semana para um numero
        String[] semanaArray = context.getResources().getStringArray(R.array.nomes_semana);
        for (int i = 1; i <= semanaArray.length; i++) {
            semanaNumeroMap.put(semanaArray[i - 1], String.valueOf(i));
        }
    }
    public boolean passagem_de_hora(){
        long hora_passada = System.nanoTime();
//        6e10 = 1 minuto
        boolean verificador_da_passagem = hora_passada - this.hora_inicial > 6e10;
        if (verificador_da_passagem){
            this.hora_inicial = hora_passada;
        }
        return verificador_da_passagem;
    }
    
    String[] recetorTempo() {
        String[] hora = DateFormat.getTimeInstance().format(new Date()).split(":");
        String[] data = DateFormat.getDateInstance().format(new Date()).split("/");
        String[] mensagem = {
                hora[0], hora[1], hora[2],
                data[0], data[1], data[2],
                semanaNumeroMap.get(DateFormat.getDateInstance(DateFormat.FULL)
                        .format(new Date()).split(",")[0])
        };
        return mensagem;
    }

}
