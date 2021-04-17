package com.hswatch.refactor;

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

import androidx.annotation.Nullable;

import com.hswatch.MainActivity;
import com.hswatch.R;

import static com.hswatch.App.CANAL_SERVICO;
import static com.hswatch.Utils.BT_DEVICE_NAME;
import static com.hswatch.Utils.REQUEST_CODE_PENDING_INTENT;
import static com.hswatch.Utils.REQUEST_CODE_START_FOREGROUND;

//TODO(documentar)
public class MainServico extends Service {

    private static final String TAG = "Servico_tenta_dar_log";

    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    private static final int NULL_STATE = 0;

    private int currentState = 0;

    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;

    private ThreadConnection threadConnection;
    private ThreadConnected threadConnected;

//    private BroadcastReceiverMainServico broadcastReceiverMainServico;

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        broadcastReceiverMainServico = new BroadcastReceiverMainServico();
//    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //region BroadcastReceiver Flags
//        IntentFilter intentFilter = new IntentFilter();
        // A flag to tell if we are still connected to a device or not
//        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        //
//        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        //endregion

//        Trying to get a name to the device and then show a notification
//        TODO(Melhorar este código)
        String deviceName;
        try {
            deviceName = intent.getStringExtra(BT_DEVICE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
            return START_STICKY;
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE_PENDING_INTENT,
                notificationIntent, 0);
        Notification notification = new Notification.Builder(this, CANAL_SERVICO)
                .setContentTitle(String.format(getResources().getString(R.string.ServiceBT_Title), deviceName))
                .setContentText(getResources().getString(R.string.ServiceBT_Text))
                .setStyle(new Notification.BigTextStyle().bigText(getResources().getString(R.string.ServiceBT_Text)))
                .setSmallIcon(R.drawable.ic_bluetooth_connected_green_24dp)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(REQUEST_CODE_START_FOREGROUND, notification);

        Log.d(TAG, "onStartCommand: " + deviceName);

        for (BluetoothDevice device : BluetoothAdapter.getDefaultAdapter().getBondedDevices()) {
            if (device.getName().equals(deviceName)) {
                this.bluetoothDevice = device;
                break;
            }
        }

//        // Setup GPS Listener so the Weather API could work with GPS
//        setupGPSListener();

        //TODO(adicionar broadcast receiver para saber da ligação bt: bt on e off e se está fora de
        // alcance: https://developer.android.com/reference/android/bluetooth/BluetoothAdapter.html#ACTION_CONNECTION_STATE_CHANGED)

        createConection(this.bluetoothDevice);

        return START_REDELIVER_INTENT;
    }

    //region BT Connections

    private void createConection(BluetoothDevice bluetoothDevice) {
        if (getCurrentState() == STATE_CONNECTING && this.threadConnection != null) {
            threadConnection.restart();
            setThreadConnection(null);
        }

        if (this.threadConnected != null) {
            this.threadConnected.cancel();
            setThreadConnected(null);
        }

        this.threadConnection = new ThreadConnection(bluetoothDevice, this);
        this.threadConnection.start();
    }

    public void establishConnection() {
        if (this.threadConnection != null) {
            setThreadConnection(null);
        }

        this.threadConnected = new ThreadConnected(this);
        this.threadConnected.start();
    }

    public void lostConnectionAtInitialThread() {
        if (this.threadConnected != null) {
            setThreadConnected(null);
            setCurrentState(NULL_STATE);
            stopSelf();
        }
    }

    public void connectionFailed() {
        if (this.threadConnection != null && getCurrentState() == STATE_CONNECTING) {
            setCurrentState(NULL_STATE);
            setThreadConnection(null);
            stopSelf();
        }
    }

    public void lostConnection() {
        if (this.threadConnected != null) {
            threadConnected.cancel();
            setThreadConnected(null);
            setCurrentState(NULL_STATE);
            stopSelf();
        }
    }

    //endregion

//    public class BroadcastReceiverMainServico extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//        }
//
//    }


    //region Getters and Setters
    public Context getCurrentContext() {
        return getApplicationContext();
    }

    public BluetoothDevice getBluetoothDevice() {
        return this.bluetoothDevice;
    }

    public int getCurrentState() {
        return this.currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    public boolean isConnected() {
        return getCurrentState() == STATE_CONNECTED;
    }

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
    }

    public void setThreadConnection(ThreadConnection threadConnection) {
        this.threadConnection = threadConnection;
    }

    public void setThreadConnected(ThreadConnected threadConnected) {
        this.threadConnected = threadConnected;
    }
    //endregion
}
