package com.hswatch.bluetooth;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.hswatch.MainActivity;
import com.hswatch.R;
import com.hswatch.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.hswatch.App.SERVICO_CHANNEL;
import static com.hswatch.Utils.BT_DEVICE_NAME;
import static com.hswatch.Utils.NOTIFICATION_SERVICE_ID;
import static com.hswatch.Utils.REQUEST_CODE_PENDING_INTENT;
import static com.hswatch.Utils.FOREGROUND_ID;

//TODO(documentar)
public class MainServico extends Service {

    public static final int NULL_STATE = 0;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    public static final int OUT_OF_RANGE = 4;

    private int currentState = 0;

    private boolean flagError = false;
    private boolean flagReconnection = false;
    private static boolean flagInstante = false;

    private String deviceName;

    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;

    private ThreadConnection threadConnection;
    private static ThreadConnected threadConnected;
    private ThreadReconnection threadReconnection;

    private BroadcastReceiverMainServico broadcastReceiverMainServico;

    private SharedPreferences mainSettings;

    @Override
    public void onCreate() {
        super.onCreate();
        broadcastReceiverMainServico = new BroadcastReceiverMainServico();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiverMainServico);
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
        // A flag to check if the service is still connected to a device or not
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        //region Test
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        //endregion

        // Register BroadCastReceiverMainServico
        registerReceiver(broadcastReceiverMainServico, intentFilter);
        
        //endregion

//        Trying to get a name to the device and then show a notification
        try {
            deviceName = intent.getStringExtra(BT_DEVICE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
            return START_STICKY;
        }


        startForeground(FOREGROUND_ID, createForegroundNotification(
                String.format(getResources().getString(R.string.ServiceBT_Title), deviceName),
                getResources().getString(R.string.ServiceBT_Text)
        ));

        for (BluetoothDevice device : BluetoothAdapter.getDefaultAdapter().getBondedDevices()) {
            if (device.getName().equals(deviceName)) {
                this.bluetoothDevice = device;
                break;
            }
        }

        createConection(this.bluetoothDevice);

        this.mainSettings = PreferenceManager.getDefaultSharedPreferences(getCurrentContext());

        return START_REDELIVER_INTENT;
    }


    /**
     * Returns a Foreground Notification with a title, contentText and linked to the MainActivity,
     * where the connection can be managed with the user. The contentText is shown in two version,
     * one for small text and another for a bigger one, so it can show the user more information if
     * he or she wants it.
     *
     * @param title The Foreground Notification's title, shown at the top of the notification
     * @param contentText The Foreground Notification's description, shown at the bottom and can be
     *                    used to show more information about the connection
     * @return The Foreground Notification used to show the MainServico running in the background
     */
    @NonNull
    private Notification createForegroundNotification(String title, String contentText) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                REQUEST_CODE_PENDING_INTENT,
                notificationIntent, 0);

        return new Notification.Builder(this, SERVICO_CHANNEL)
                .setContentTitle(title)
                .setContentText(contentText)
                .setStyle(new Notification.BigTextStyle().bigText(contentText))
                .setSmallIcon(R.drawable.ic_bluetooth_connected_green_24dp)
                .setContentIntent(pendingIntent)
                .build();
    }


    private void createNotification(String title, String contentText) {
        Notification notification = new NotificationCompat.Builder(this, SERVICO_CHANNEL)
                .setContentTitle(title)
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                .setSmallIcon(R.drawable.ic_bluetooth_connected_green_24dp)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat
                .from(getCurrentContext());
        notificationManager.notify(NOTIFICATION_SERVICE_ID, notification);
    }

    //region BT Connections

    public void createConection(BluetoothDevice bluetoothDevice) {
        if (getCurrentState() == STATE_CONNECTING && this.threadConnection != null) {
            threadConnection.restart();
            setThreadConnection(null);
        }

        if (threadConnected != null) {
            threadConnected.cancel();
            setThreadConnected(null);
        }

        this.threadConnection = new ThreadConnection(bluetoothDevice, this);
        this.threadConnection.start();
    }

    public void connectionEstablish() {
        if (!this.isFlagError()) {
            if (this.threadConnection != null) {
                setThreadConnection(null);
            }

            threadConnected = new ThreadConnected(this);
            threadConnected.start();
        }
    }

    /**
     * If the user wants to reconnect the app to the device, but it's the first connection
     * made, then cancel the service. If not, cancel the service so there is no reconnection
     * to be made.
     */
    private void stopConnection() {
        if (this.mainSettings.getBoolean("connection", true)) {
            if (!isFlagReconnection()) {
                MainServico.setFlagInstante(false);
                stopForeground(true);
                stopSelf();
            }
        } else {
            MainServico.setFlagInstante(false);
            stopForeground(true);
            stopSelf();
        }
    }

    public void connectionFailed() {
        if (this.threadConnection != null && getCurrentState() == STATE_CONNECTING) {
            flagError = true;
            notificationError();
            setCurrentState(NULL_STATE);
            setThreadConnection(null);
            stopConnection();
        }
    }

    public void connectionLostAtInitialThread() {
        if (threadConnected != null) {
            flagError = true;
            notificationError();
            setThreadConnected(null);
            setCurrentState(NULL_STATE);
            stopConnection();
        }
    }

    public void connectionLost() {
        if (threadConnected != null) {
            flagError = true;
            notificationErrorReconection();
            threadConnected.cancel();
            setThreadConnected(null);
            setCurrentState(OUT_OF_RANGE);
            setReconnectionThread();
        }
    }

    private void setReconnectionThread() {
        if (!this.mainSettings.getBoolean("connection", true)) {
            setFlagReconnection(false);
        } else {
            setFlagReconnection(true);
            notificationChangeService();
            threadReconnection = new ThreadReconnection(this);
            threadReconnection.start();
        }
    }

    public void bluetoothIsOFF() {
        setFlagReconnection(false);
        setCurrentState(NULL_STATE);
        createNotification(
                getResources().getString(R.string.ServiceBT_BT_Off_Title),
                getResources().getString(R.string.ServiceBT_BT_Off_ContextText)
        );
        if (threadConnection != null) {
            setThreadConnection(null);
        }
        if (threadConnected != null) {
            threadConnected.cancel();
            setThreadConnected(null);
        }
        stopForeground(true);
        MainServico.setFlagInstante(false);
        stopSelf();
    }

    private void notificationError() {
        createNotification(getResources().getString(R.string.ServiceBT_BT_Error_Title),
                getResources().getString(R.string.ServiceBT_BT_Error_ContextText));
    }

    private void notificationErrorReconection(){
        createNotification(getResources().getString(R.string.ServiceBT_BT_Error_Title),
                getResources().getString(R.string.ServiceBT_BT_Error_ContextText) +
                        getResources().getString(R.string.ServiceBT_BT_Error_Reconection));
    }

    public void notificationChangeService() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (isFlagReconnection()) {
            mNotificationManager.notify(FOREGROUND_ID, createForegroundNotification(
                    String.format(getResources().getString(R.string.Foreground_Reconection_Title), this.getBluetoothDevice().getName()),
                    getResources().getString(R.string.Foreground_Reconection_Text)));
        } else {
            mNotificationManager.notify(FOREGROUND_ID, createForegroundNotification(
                    String.format(getResources().getString(R.string.ServiceBT_Title), deviceName),
                    getResources().getString(R.string.ServiceBT_Text)));
        }

    }

    public void threadInterrupted(Thread thread) {
        if (thread instanceof ThreadConnected) {
            ((ThreadConnected) thread).cancel();
        }
        MainServico.setFlagInstante(false);
        stopForeground(true);
        stopSelf();
    }
    //endregion

    public class BroadcastReceiverMainServico extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            final String action = intent.getAction();
            if (action != null) {
                // Stop MainServico and ThreadConnected in case the Bluetooth is turn off
                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                            == BluetoothAdapter.STATE_OFF) {
                        bluetoothIsOFF();
                    }
                }
            }
        }
    }

    public static void sendTime() {
        if (threadConnected != null) {
            threadConnected.sendTime();
        }
    }

    public static void sendNotification(List<String> message) {
        if (threadConnected != null) {
            threadConnected.sendMessage(message);
        }
    }

    public static void sendCalls(String number, String callingName, String receivedHour, String callingState) {
        if (threadConnected != null) {
            ArrayList<String> callMessage = new ArrayList<String>(){{
                add(Utils.NOT_INDICATOR);
                add(Utils.INDICADOR_TEL);
                add(receivedHour.split(":")[0]);
                add(receivedHour.split(":")[1]);
                add(callingName + "@" + number);
                add(callingState);
            }};
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
        return getCurrentState() == STATE_CONNECTED &&
                this.mainSettings.getBoolean("connection", true);
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

    public static void setThreadConnected(ThreadConnected threadConnected) {
        MainServico.threadConnected = threadConnected;
    }

    public void setThreadReconnection(ThreadReconnection threadReconnection) {
        this.threadReconnection = threadReconnection;
    }

    public boolean isFlagReconnection() {
        return flagReconnection;
    }

    public void setFlagReconnection(boolean flagReconnection) {
        if (!flagReconnection)
            this.setThreadReconnection(null);

        this.flagReconnection = flagReconnection;
    }

    public boolean isFlagError() {
        return flagError;
    }

    public static boolean isFlagInstante() {
        return flagInstante;
    }

    public static void setFlagInstante(boolean flagInstante) {
        MainServico.flagInstante = flagInstante;
    }

    public void setFlagError(boolean flagError) {
        this.flagError = flagError;
    }

    public SharedPreferences getMainSettings() {
        return mainSettings;
    }

    public void setMainSettings(SharedPreferences mainSettings) {
        this.mainSettings = mainSettings;
    }

    //endregion
}