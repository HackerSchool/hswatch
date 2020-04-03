package com.hswatch.bluetooth;

public class Objeto {
    public static final String TAG = "com.Objeto";
    private long hora_inicial;
//    int hora = 3600000;
//    private double hora = 6e10; // 1 minuto

    public Objeto(){
        hora_inicial = System.nanoTime();
    }

    public boolean passagem_de_hora(){
     long hora_passada = System.nanoTime();
     boolean verificador_da_passagem = hora_passada - this.hora_inicial > 6e10;
//     Log.i(TAG, "Hora inicial: " + this.hora_inicial / 1000 + "\t Hora passada: " + hora_passada / 1000 + "\t Verificador: " + verificador_da_passagem);
     if (verificador_da_passagem){
         this.hora_inicial = hora_passada;
     }
     return verificador_da_passagem;
    }
}
