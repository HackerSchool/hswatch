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

    private static final int NULL_STATE = 0;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    private static final int ON_HOLD = 4;

    private int currentState = 0;

    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;

    private ThreadConnection threadConnection;
    private static ThreadConnected threadConnected;

    private boolean connectionEstablished = false;

    private BroadcastReceiverMainServico broadcastReceiverMainServico;

    @Override
    public void onCreate() {
        super.onCreate();
        broadcastReceiverMainServico = new BroadcastReceiverMainServico();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //region BroadcastReceiver Flags
        IntentFilter intentFilter = new IntentFilter();
//        // A flag to check if the service is still connected to a device or not
//        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        // A flag to check if the Bluetooth is On or Off
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        // A flag to check if the Bluetooth Device is in or out of range
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        // Register BroadCastReceiverMainServico
        registerReceiver(broadcastReceiverMainServico, intentFilter);

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

        if (threadConnected != null) {
            threadConnected.cancel();
            setThreadConnectedToNull();
        }

        this.threadConnection = new ThreadConnection(bluetoothDevice, this);
        this.threadConnection.start();
    }

    public void connectionEstablish() {
        if (this.threadConnection != null) {
            setThreadConnection(null);
        }

        threadConnected = ThreadConnected.getInstance(this);
        threadConnected.start();
    }

    public void connectionLostAtInitialThread() {
        if (threadConnected != null) {
            setThreadConnectedToNull();
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

    public void connectionLost() {
        if (threadConnected != null) {
            threadConnected.cancel();
            setThreadConnectedToNull();
            setCurrentState(NULL_STATE);
            stopSelf();
        }
    }

    /**
     * Stop the ThreadConnected because the connection is lost, but could be re-established and not
     * end the service.
     */
    private void connectionStopped() {
        if (threadConnected != null) {
            threadConnected.cancel();
            setThreadConnectedToNull();
            setCurrentState(ON_HOLD);
        }
    }

    //endregion

    public class BroadcastReceiverMainServico extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                                == BluetoothAdapter.STATE_OFF) {
                            connectionLost();
                        }
                        break;
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        if (isConnectionEstablished()) {
                            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            if (device.getName().equals(threadConnected.getWatchName())) {
                                connectionStopped();
                                //todo(criar um workjobscheduler para criar uma nova conexão)
//                                PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest().build();
                            }
                        }
                    default:break;
                }
            }
        }
    }

    public static void sendTime() {
        if (threadConnected != null) {
            threadConnected.sendTime();
        }
    }


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

    public void setThreadConnectedToNull() {
        ThreadConnected.setINSTANCEToNull();
    }

    public boolean isConnectionEstablished() {
        return connectionEstablished;
    }

    public void setConnectionEstablished(boolean connectionEstablished) {
        this.connectionEstablished = connectionEstablished;
    }

    //endregion
}
