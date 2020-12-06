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
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.hswatch.MainActivity;
import com.hswatch.R;
import com.hswatch.worker.HoraWorker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.hswatch.App.CANAL_SERVICO;
import static com.hswatch.Constantes.ACAO_DEFINICOES_SERVICO;
import static com.hswatch.Constantes.ACAO_NOTIFICACOES_SERVICO;
import static com.hswatch.Constantes.ACAO_SERVICO_TEMPO_API;
import static com.hswatch.Constantes.INDICADOR_CLIMA;
import static com.hswatch.Constantes.INDICADOR_TEL;
import static com.hswatch.Constantes.delimitador;
import static com.hswatch.Constantes.separador;
import static com.hswatch.Constantes.uid;

public class Servico extends Service {

//    TAG
    public static final String TAG = "hswatch.service.Servico";

//    Estado
    private int estadoAtual;

//    Bluetooth
    private BluetoothDevice dispositivoBluetooth;

//    BroadcastReceiver
    private Recetor recetor;

//    Thread's
    private ThreadConexao threadConexao;
    private static ThreadConectado threadConectado;

//    Inicialização de variáveis
    private char[] caracteres = new char[3];
    private String mensagemRecebida;
    private String dispositivoEscolhido;

//    Perfile do dispositivo
    private Profile profileDispositivo;
//    WorkManagers
    private WorkInfo horaWorker;

//    Chamadas
    private static byte[][] mensagemChamada = {
            "NOT".getBytes(),                                                           // 0
            separador,                                                                  // 1
            INDICADOR_TEL.getBytes(),                                                   // 2
            separador,                                                                  // 3
            "hora".getBytes(),                                                          // 4
            separador,                                                                  // 5
            "min".getBytes(),                                                           // 6
            separador,                                                                  // 7
            "nome".getBytes(),                                                          // 8
            separador,                                                                  // 9
            "estado".getBytes(),                                                        // 10
            delimitador                                                                 // 11
    };

//    Verificar se o Serviço está a correr
    public static boolean ativoServico = false;

/*
 *
 * Com isto tudo implementado, rever o código feito na totalidade e ver se se pode eliminar alguns
 * pontos ou simplificar operações. Se for feita esta lista, considera-se a aplicação acabada por FIM!!!
 *
 * */
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
        ativoServico = false;
        unregisterReceiver(recetor);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        Iniciar o BroadcastReceiver para verificar conexões e trocas de mensagens
        IntentFilter intentFilterServico = new IntentFilter();
        intentFilterServico.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilterServico.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilterServico.addAction(ACAO_NOTIFICACOES_SERVICO);
        intentFilterServico.addAction(ACAO_SERVICO_TEMPO_API);
        intentFilterServico.addAction(ACAO_DEFINICOES_SERVICO);
        intentFilterServico.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(recetor, intentFilterServico);

        try{
            dispositivoEscolhido = intent.getStringExtra(getResources().getString(R.string.ServicoDisp));
        } catch (NullPointerException e){
            Log.e(TAG, "Erro: " + e.toString());
        }

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

//        Start GPSListener

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
//    private void tempo () {
//        if (horaWorker == null) { return; }
//        String[] mensagem = horaWorker.getOutputData().getStringArray("its_time_to_stop");
//        if (mensagem == null) {
//            return;
//        }
//        if (threadConectado != null) {
//            threadConectado.escrever("TIM".getBytes());
//            for (String msg : mensagem) {
//                threadConectado.escrever(separador);
//                threadConectado.escrever(msg.getBytes());
//            }
//            threadConectado.escrever(delimitador);
//        }
//    }

    private void tempo (Profile profile) {
        String[] mensagem = profile.recetorTempo();
        if (threadConectado != null) {
            threadConectado.escrever("TIM".getBytes());
            for (String msg : mensagem) {
                threadConectado.escrever(separador);
                threadConectado.escrever(msg.getBytes());
            }
            threadConectado.escrever(delimitador);
        }
    }

    private boolean recebeuTempo() {
        boolean running = false;
        try {
            List<WorkInfo> workInfoList = WorkManager.getInstance(getApplicationContext()).
                    getWorkInfosByTag("tag_horas").get();
            for (WorkInfo workInfo : workInfoList) {
                running = workInfo.getState() == WorkInfo.State.RUNNING | workInfo.getState() == WorkInfo.State.ENQUEUED;
                if (running)
                    horaWorker = workInfo;
                break;
            }
            return running;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Funções das chamadas
//    private void obterNumeroEstado(Context context, Intent intent) {
//        String number = Objects.requireNonNull(intent.getExtras())
//                .getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
//        String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
//        if (number != null) {
//            number = obterNomePorNumero(context, number);
//            int estado = 10;
//            if (Objects.equals(stateStr, TelephonyManager.EXTRA_STATE_IDLE)) {
//                estado = TelephonyManager.CALL_STATE_IDLE;
//            } else if (Objects.equals(stateStr, TelephonyManager.EXTRA_STATE_OFFHOOK)) {
//                estado = TelephonyManager.CALL_STATE_OFFHOOK;
//            }
//            else if(Objects.equals(stateStr, TelephonyManager.EXTRA_STATE_RINGING)){
//                estado = TelephonyManager.CALL_STATE_RINGING;
//            }
//
//            verificadorEstado(context, estado, number);
//        }
//    }
//
//    private String obterNomePorNumero(Context context, String number) {
//        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
//                Uri.encode(number));
//
//        String[] projecao = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
//
//        Cursor cursor = context.getContentResolver().query(uri, projecao, null, null);
//
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                return cursor.getString(0);
//            }
//            cursor.close();
//        }
//        return number;
//    }
//
//    public void verificadorEstado(Context context, int estado, String number) {
//        if (estadoAnterior != estado) {
//            /*
//             * Estado 0: Não há chamada - IDLE
//             * Estado 1: Receber a chamada - RINGING
//             * Estado 2: Chamada a decorrer - OFFHOOK
//             */
//            switch (estado) {
//                case TelephonyManager.CALL_STATE_RINGING:
//                    estaReceber = true;
//                    comecoChamada = new Date();
////                    onIncomingCallStarted(context, number, comecoChamada);
//                    break;
//                case TelephonyManager.CALL_STATE_OFFHOOK:
//                    if (estadoAnterior != TelephonyManager.CALL_STATE_RINGING) {
//                        estaReceber = false;
//                    }
//                case TelephonyManager.CALL_STATE_IDLE:
//                    if (estadoAnterior == TelephonyManager.CALL_STATE_RINGING) {
////                        onMissedCall(context, number, comecoChamada);
//                    } else if (estaReceber) {
////                        onCallEnded(context, number, comecoChamada, new Date());
//                    }
//                    break;
//                default:break;
//            }
//            estadoAnterior = estado;
//        }
//    }
    public static void receberChamada(String numero, String nome, String hora) {
        mensagemChamada[4] = hora.split(":")[0].getBytes();
        mensagemChamada[6] = hora.split(":")[0].getBytes();
        mensagemChamada[8] = (nome + " @ " + numero).getBytes();
        mensagemChamada[10] = "receber".getBytes();

        enviarMensagensRelogio(mensagemChamada);
    }

    public static void perdidaChamada(String numero, String nome,  String hora) {
        mensagemChamada[4] = hora.split(":")[0].getBytes();
        mensagemChamada[6] = hora.split(":")[0].getBytes();
        mensagemChamada[8] = (nome + " @ " + numero).getBytes();
        mensagemChamada[10] = "perdida".getBytes();

        enviarMensagensRelogio(mensagemChamada);
    }
    // Fim das Funções das chamadas


    public class Recetor extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String acao = intent.getAction();
            if (acao == null) {
                return;
            }
            switch (acao) {
                // Chaves do Bluetooth
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR) ==
                            BluetoothAdapter.STATE_OFF) {
                        stopSelf();
                    }
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    conexaoPerdida();
                    break;

                // Chave da comunicação entre o NotificationListener para o Serviço
//                case ACAO_NOTIFICACOES_SERVICO:
//                    if (threadConectado != null) {
//                        try{
//                            threadConectado.escrever(intent.getByteArrayExtra(ELEMENTO_SERVICO_NOT));
//                        } catch (Exception e){
//                            stopSelf();
//                            Log.e(TAG, "Serviço: Erro ao escrever!", e);
//                        }
//                    }
//                    break;
//                case TelephonyManager.ACTION_PHONE_STATE_CHANGED:
//                    obterNumeroEstado(context, intent);
//                    break;
                default:break;
            }
        }
    }

    public static void enviarMensagensRelogio(byte[][] mensagem) {
        if (threadConectado != null) {
            for (byte[] bytes : mensagem) {
                threadConectado.escrever(bytes);
            }
        }
    }

    public static void enviarMeteorologiaRelogio(List<String> mensagem) {
        if (threadConectado != null) {
            threadConectado.escrever(INDICADOR_CLIMA.getBytes());
            for (String conteudo : mensagem) {
                threadConectado.escrever(separador);
                threadConectado.escrever(conteudo.getBytes());
            }
            threadConectado.escrever(delimitador);
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
                e.printStackTrace();
                conexaoPerdida();
            }
            inputStream = inputStreamL;
            outputStream = outputStreamL;
            estadoAtual = getResources().getInteger(R.integer.ESTADO_CONECTADO);
            Intent sinalVerde = new Intent(getResources().getString(R.string.LISTAR_FRAG));
            sinalVerde.putExtra(getResources().getString(R.string.SINAL_VERDE), true);
            sinalVerde.putExtra(getResources().getString(R.string.NOME), dispositivoEscolhido);
            sendBroadcast(sinalVerde);
            profileDispositivo = new Profile(getApplicationContext(), dispositivoBluetooth.getName());
            int periodo = Integer.parseInt(Objects.requireNonNull(PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext())
                    .getString("horas", "15")));
            periodo = Math.max(periodo, 15);
            PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(HoraWorker.class,
                    periodo, TimeUnit.MINUTES).build();
            WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork(
                    "tag_horas", ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);
            ativoServico = true;
        }

        @Override
        public void run() {
            byte bytes;
            int bufferposition = 0;
            tempo(profileDispositivo);
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
                            if (delimitador[0] == bytes) {
                                mensagemRecebida = new String(caracteres);
                                bufferposition = 0;
                            } else {
                                try {
                                    caracteres[bufferposition++] = (char) bytes;
                                } catch (Exception e){
                                    Log.e(TAG, "Erro: " + e.toString());
                                }
                            }
                        }
                        try{
                            if (mensagemRecebida != null) {
                                Log.v(TAG, mensagemRecebida);
                            } else {
                                continue;
                            }
                        } catch (NullPointerException e) {
                            Log.e(TAG, "Erro: " + e.toString());
                        }
                        if (Objects.equals(mensagemRecebida, INDICADOR_CLIMA)) {
                            profileDispositivo.jsonParserTempo(respostaLimpa -> {
                                enviarMeteorologiaRelogio(respostaLimpa);
                                caracteres = new char[3];
                                mensagemRecebida = "";
                            });
                        }
                    }
                } catch (IOException e){
                    Log.e(TAG, "Algo correu mal na leitura", e);
                    conexaoPerdida();
                    break;
                }
//                if (recebeuTempo())
//                    tempo();
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
