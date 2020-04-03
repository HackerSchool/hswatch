package com.hswatch.bluetooth;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.hswatch.MainActivity;
import com.hswatch.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.hswatch.App.CANAL_SERVICO;

public class Servico extends Service {

//    TAG
    public static final String TAG = "hswatch.service.Servico";

//    UUID
    public static final UUID uid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

//    Estado
    private int estadoAtual;

//    Bluetooth
    BluetoothDevice dispositivoBluetooth;

//    BroadcastReceiver
    private Recetor recetor;

//    Thread's
    ThreadConexao threadConexao;
    ThreadConectado threadConectado;

//    Inicialização de variáveis
    Character[] caracteres;
    StringBuilder mensagemRecebida;
    Map<String, String> semana = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        recetor = new Recetor();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(recetor);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        Iniciar o BroadcastReceiver para verificar conexões e trocas de mensagens
        IntentFilter intentFilterServico = new IntentFilter();
        intentFilterServico.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilterServico.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(recetor, intentFilterServico);

        String dispositivoEscolhido = intent.getStringExtra(getResources().getString(R.string.ServicoDisp));

        Intent notificacaoIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificacaoIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CANAL_SERVICO)
                .setContentTitle("Está agora conectado a " + dispositivoEscolhido + "!")
                .setContentText("Carregue para alterar as definições ou efetuar outra conexão nova.")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Carregue para alterar as definições ou efetuar outra conexão nova."))
                .setSmallIcon(R.drawable.ic_bluetooth_connected_green_24dp)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
        startForeground(3, notification);

//        https://developer.android.com/training/notify-user/expanded

//        Procurar Bluetooth device pretendido
        for (BluetoothDevice dispositivo : BluetoothAdapter.getDefaultAdapter().getBondedDevices()) {
            if (dispositivo.getName().equals(dispositivoEscolhido)) {
                dispositivoBluetooth = dispositivo;
                break;
            }
        }
        String[] semanaArray = getResources().getStringArray(R.array.nomes_semana);
        for (int i = 1; i <= semanaArray.length; i++) {
            semana.put(semanaArray[i - 1], String.valueOf(i));
        }

//        Iniciar Thread de conexão
        criarConexao(dispositivoBluetooth);

        return START_STICKY;
    }

    private void criarConexao(BluetoothDevice dispositivoEscolhido) {
        if (estadoAtual == getResources().getInteger(R.integer.ESTADO_CONECTANDO) && threadConexao != null) {
            threadConexao.cancel();
            threadConexao = null;
        }
        if (threadConectado != null){
            threadConectado.cancel();
            threadConectado = null;
        }

        threadConexao = new ThreadConexao (dispositivoEscolhido);
        threadConexao.start();
    }

    private void conexaoFalhada() {
        if (threadConexao != null && estadoAtual == getResources().getInteger(R.integer.ESTADO_CONECTANDO)) {
            estadoAtual = getResources().getInteger(R.integer.ESTADO_NULO);
            threadConexao.cancel();
            stopSelf();
        }
    }

    private void estabelecerConexao(BluetoothSocket bluetoothSocket) {
        if (threadConexao != null){
            threadConexao.cancel();
            threadConexao = null;
        }
        if (threadConectado != null){
            threadConectado.cancel();
            threadConectado = null;
        }

        threadConectado = new ThreadConectado(bluetoothSocket);
        threadConectado.start();
    }

    private void conexaoPerdida() {
        if (threadConectado != null){
            threadConectado.cancel();
            threadConectado = null;
            estadoAtual = getResources().getInteger(R.integer.ESTADO_NULO);
            stopSelf();
        }
    }
    void tempo () {
        String[] mensagem = {
                DateFormat.getTimeInstance().format(new Date()).split(":")[0],
                DateFormat.getTimeInstance().format(new Date()).split(":")[1],
                DateFormat.getTimeInstance().format(new Date()).split(":")[2],
                DateFormat.getDateInstance().format(new Date()).split("/")[0],
                DateFormat.getDateInstance().format(new Date()).split("/")[1],
                DateFormat.getDateInstance().format(new Date()).split("/")[2],
                semana.get(DateFormat.getDateInstance(DateFormat.FULL)
                        .format(new Date()).split(",")[0])
        };
        if (threadConectado != null) {
            threadConectado.escrever("TIM".getBytes());
            for (String msg : mensagem) {
                threadConectado.escrever(NotificationListener.separador);
                threadConectado.escrever(msg.getBytes());
            }
            threadConectado.escrever(NotificationListener.delimitador);
        }
    }

    public class Recetor extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String acao = intent.getAction();
            if (acao == null) {
                return;
            }
            switch (acao) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR) ==
                            BluetoothAdapter.STATE_OFF) {
                        stopSelf();
                    }
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    Toast.makeText(getApplicationContext(), "Perdida a conexão Bluetooth", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class ThreadConexao extends Thread {
        private final BluetoothSocket bluetoothSocket;

        ThreadConexao(BluetoothDevice dispositivo) {
            BluetoothSocket socket = null;
            try {
                socket = dispositivo.createRfcommSocketToServiceRecord(uid);
            } catch (IOException e) {
                Log.e(TAG, "Ocorreu um erro IOException.", e);
                conexaoFalhada();
            }
            bluetoothSocket = socket;
            estadoAtual = getResources().getInteger(R.integer.ESTADO_CONECTANDO);
        }

        @Override
        public void run() {
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            try {
                bluetoothSocket.connect();
            } catch (IOException e) {
                try {
                    Log.e(TAG, "Não foi possível conectar o socket da ligação!", e);
                    bluetoothSocket.close();
                    Intent sinalVerde = new Intent(getResources().getString(R.string.LISTAR_FRAG));
                    sinalVerde.putExtra(getResources().getString(R.string.SINAL_VERDE), false);
                    sendBroadcast(sinalVerde);
                    conexaoFalhada();
                    return;
                } catch (IOException ex) {
                    Log.e(TAG, "Algo aconteceu...", ex);
                }
            }
            synchronized (Servico.this) {
                threadConexao = null;
            }

            estabelecerConexao(bluetoothSocket);
        }

        void cancel() {
            try{
                bluetoothSocket.close();
            } catch(IOException e){
                Log.e(TAG, "Algo deu mal...", e);
            }
        }
    }

    class ThreadConectado extends Thread{

        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        ThreadConectado(BluetoothSocket bluetoothsocket) {
            bluetoothSocket = bluetoothsocket;
            InputStream inputStreamL = null;
            OutputStream outputStreamL = null;
            try {
                inputStreamL = bluetoothsocket.getInputStream();
                outputStreamL = bluetoothsocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Falha ao obter bluetooth streams.", e);
                conexaoPerdida();
            }
            inputStream = inputStreamL;
            outputStream = outputStreamL;
            estadoAtual = getResources().getInteger(R.integer.ESTADO_CONECTADO);
            Intent sinalVerde = new Intent(getResources().getString(R.string.LISTAR_FRAG));
            sinalVerde.putExtra(getResources().getString(R.string.SINAL_VERDE), true);
            sendBroadcast(sinalVerde);
        }

        @Override
        public void run() {
            byte bytes;
            int bufferposition = 0;
            tempo();
            while (estadoAtual == getResources().getInteger(R.integer.ESTADO_CONECTADO)){
                try{
                    int bytesavailable = inputStream.available();
                    if (bytesavailable > 0){
                        byte[] buffer = new byte[bytesavailable];
                        if (inputStream.read(buffer) == -1) {
                            return;
                        }
                        for (int i=0; i<bytesavailable; i++){
                            bytes = buffer[i];
                            if (bufferposition == 7) {
                                for (Character caractere : caracteres) {
                                    if (caractere != null) {
                                        mensagemRecebida.append(caractere.toString());
                                    }
                                }
                                bufferposition = 0;
                            } else {
                                caracteres[bufferposition++] = (char) bytes;
                            }
                        }
                    }
                } catch (IOException e){
                    Log.e(TAG, "Algo correu mal na leitura", e);
                    conexaoPerdida();
                    break;
                }
            }
        }

        void escrever(byte[] buffer){
            try{
                outputStream.write(buffer);
            } catch (IOException e){
                Log.e(TAG, "Não foi possível escrever para o dispositivo", e);
            }
        }

        void cancel (){
            try{
                bluetoothSocket.close();
            } catch(IOException e){
                Log.e(TAG, "Não foi possivel fechar socket na conexão", e);
            }
        }
    }
}
