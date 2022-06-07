package io.hackerschool.hswatch_connection_module;

import android.annotation.SuppressLint;
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
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;

import io.hackerschool.hswatch_connection_module.connection_threads.HSWThreadConnected;
import io.hackerschool.hswatch_connection_module.connection_threads.HSWThreadConnection;
import io.hackerschool.hswatch_connection_module.connection_threads.HSWThreadReconnection;
import io.hackerschool.hswatch_connection_module.connection_threads.HSWThreadTestConnection;
import io.hackerschool.hswatch_connection_module.flags.HSWFlag;
import io.hackerschool.hswatch_connection_module.flags.HSWStatusFlag;

public class HSWService extends Service {

    /**
     * The flag that tells in which state is the connection status, where it can vary between
     * a no-state connection, connecting state, connection establish state, out-of-range state and
     * reconnecting state
     */
    public static HSWStatusFlag connectionStatus;

    /**
     *
     */
    private NotificationManager notificationManager;

    /**
     *
     */
    private String deviceName;

    /**
     *
     */
    private BluetoothDevice bluetoothDevice;

    /**
     *
     */
    private BluetoothSocket bluetoothSocket;

    /**
     *
     */
    private HSWThreadConnection hswThreadConnection;

    /**
     *
     */
    public static HSWThreadConnected hswThreadConnected;

    /**
     *
     */
    private boolean flagError = false;

    /**
     *
     */
    private HSWThreadTestConnection hswThreadTestConnection;

    /**
     *
     */
    private static boolean flagReconnection = false;

    /**
     *
     */
    private boolean keepConnection = false;

    /**
     *
     */
    private HSWThreadReconnection hswThreadReconnection;

    /**
     *
     */
    private final BroadcastReceiver hswStatusBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            String action = intent.getAction();
            if (action == null) return;
            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR) == BluetoothAdapter.STATE_OFF) {
                        bluetoothIsOFF();
                    }
                case HSWFlag.HSW_KEEP_CONECTION_FROM_EXTERNAL:
                    keepConnection = intent.getBooleanExtra(
                            HSWFlag.HSW_BROADCAST_RECEIVER_EXTRA,
                            false
                    );
                    if (!keepConnection) stopConnection();
                    break;
                default:break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        sendActionInBroadcastReceiver(HSWFlag.HSW_CONNECTION_STATUS, false);

        cancelThreads();

        notificationEndService();

        unregisterReceiver(hswStatusBroadcastReceiver);

        super.onDestroy();
    }

    private void notificationEndService() {
        createNotification(
                getResources().getString(R.string.HSWatch_Notification_Title_Service_End),
                getResources().getString(R.string.HSWatch_Notification_Content_BTOFF)
        );
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Initializing the connectionStatus to a no-connection state
        connectionStatus = new HSWStatusFlag(HSWStatusFlag.NULL_STATE);

        // Initializing the notification manager object so it's possible to manage the service's
        // notifications
        this.notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        registerBroadCast();

        // Try to get the device's name which the user wants to connect via bluetooth
        try {
            this.deviceName = intent.getStringExtra(HSWFlag.HSW_DEVICE_NAME);
        } catch (Exception e) {
            sendActionInBroadcastReceiver(HSWFlag.HSW_CONNECTION_STATUS, false);
            e.printStackTrace();
            return START_STICKY;
        }

        // Creates a foreground notification to keep the service alive
        startForeground(
                HSWFlag.FOREGROUND_ID,
                createForegroundNotification(
                        String.format(
                                getResources().getString(
                                        R.string.HSWatch_Notification_Title_Connection_Started
                                ),
                                deviceName
                        ),
                        getResources().getString(
                                R.string.HSWatch_Notification_Content_Connection_Started
                        )
                )
        );

        // Search for the BT device by its name
        for (BluetoothDevice device : BluetoothAdapter.getDefaultAdapter().getBondedDevices()) {
            if (device.getName().equals(deviceName)) {
                this.bluetoothDevice = device;
                break;
            }
        }

        // Notify that a device was found
        sendActionInBroadcastReceiver(HSWFlag.HSW_DEVICE_FOUND, deviceName);

        // Check if it is important to keep the connection
        sendActionInBroadcastReceiver(HSWFlag.HSW_KEEP_CONECTION, keepConnection);

        // Notify the a connection was made
        sendActionInBroadcastReceiver(HSWFlag.HSW_CONNECTION_STATUS, true);
        createConnection(this.bluetoothDevice);

        return START_REDELIVER_INTENT;
    }

    private void registerBroadCast() {
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(HSWFlag.HSW_KEEP_CONECTION_FROM_EXTERNAL);

        registerReceiver(hswStatusBroadcastReceiver, intentFilter);

    }

    // Broadcast Receiver intent senders
    private void sendActionInBroadcastReceiver(String actionToSend, String actionExtra) {
        Intent message = new Intent(actionToSend);
        message.putExtra(HSWFlag.HSW_BROADCAST_RECEIVER_EXTRA, actionExtra);
        sendBroadcast(message);
    }

    private void sendActionInBroadcastReceiver(String actionToSend, boolean actionExtra) {
        Intent message = new Intent(actionToSend);
        message.putExtra(HSWFlag.HSW_BROADCAST_RECEIVER_EXTRA, actionExtra);
        sendBroadcast(message);
    }

    // Service notification builder

    /**
     * Returns a Foreground Notification with a title, contentText, where the connection can be
     * managed with the user. The contentText is shown in two version, one for small text and
     * another for a bigger one, so it can show the user more information if he or she wants it.
     *
     * @param title       The Foreground Notification's title, shown at the top of the notification
     * @param contentText The Foreground Notification's description, shown at the bottom and can be
     *                    used to show more information about the connection
     * @return The Foreground Notification used to show the HSWService running in the background
     */
    @NonNull
    private Notification createForegroundNotification(String title, String contentText) {
        Intent notificationIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                HSWFlag.REQUEST_CODE_PENDING_INTENT,
                notificationIntent, 0);

        return new Notification.Builder(this, HSWFlag.HSW_NOTIFICATION_CHANNEL)
                .setContentTitle(title)
                .setContentText(contentText)
                .setStyle(new Notification.BigTextStyle().bigText(contentText))
                .setSmallIcon(R.drawable.hswatch_icon)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }

    private void createNotification(String title, String contentText) {
        Notification notification = new NotificationCompat.Builder(this, HSWFlag.HSW_NOTIFICATION_CHANNEL)
                .setContentTitle(title)
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                .setSmallIcon(R.drawable.hswatch_icon)
                .build();

        this.notificationManager.notify(HSWFlag.FOREGROUND_ID, notification);
    }

    private void notificationError() {
        createNotification(getResources().getString(R.string.HSWatch_Notification_Title_Error),
                getResources().getString(R.string.HSWatch_Notification_Content_Error));
    }

    // Connection methods
    public synchronized void createConnection(BluetoothDevice bluetoothDevice) {
        if (connectionStatus.getCurrentStatus() == HSWStatusFlag.STATE_CONNECTING &&
                this.hswThreadConnection != null) {
            this.hswThreadConnection.restart();
            setHSWThreadConnection(null);
        }

        if (hswThreadConnected != null) {
            hswThreadConnected.cancel();
            setHSWThreadConnected(null);
        }

        this.hswThreadConnection = new HSWThreadConnection(bluetoothDevice, this);
        this.hswThreadConnection.start();
    }

    public synchronized void connectionEstablish() {
        if (!this.isFlagError()) {
            if (this.hswThreadConnection != null) {
                setHSWThreadConnection(null);
            }

            hswThreadConnected = new HSWThreadConnected(this);
            hswThreadConnected.start();
        }
    }

    public void connectionFailed() {
        if (this.hswThreadConnection != null &&
                connectionStatus.getCurrentStatus() == HSWStatusFlag.STATE_CONNECTING) {
            flagError = true;
            notificationError();
            setHSWThreadConnection(null);
            stopConnection();
        }
    }

    private void stopConnection() {
        if (this.keepConnection) {
            if (!isFlagReconnection()) {
                stopForeground(true);
                setConnectionStatus(HSWStatusFlag.NULL_STATE);
                sendActionInBroadcastReceiver(HSWFlag.HSW_CONNECTION_STATUS, false);
                stopSelf();
            }
        } else {
            stopForeground(true);
            setConnectionStatus(HSWStatusFlag.NULL_STATE);
            sendActionInBroadcastReceiver(HSWFlag.HSW_CONNECTION_STATUS, false);
            stopSelf();
        }

    }

    // Getters and Setters
    public void setHSWThreadConnection(HSWThreadConnection hswThreadConnection) {
        this.hswThreadConnection = hswThreadConnection;
    }

    public static HSWStatusFlag getConnectionStatus() {
        return connectionStatus;
    }

    public static void setConnectionStatus(@HSWStatusFlag.StatusFlag int connectionStatus) {
        HSWService.connectionStatus.setCurrentStatus(connectionStatus);
    }

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
    }

    public boolean isFlagError() {
        return flagError;
    }

    public void setFlagError(boolean flagError) {
        this.flagError = flagError;
    }

    public static boolean isFlagReconnection() {
        return flagReconnection;
    }

    public static void setFlagReconnection(boolean flagReconnection) {
        HSWService.flagReconnection = flagReconnection;
    }

    public void connectionLostAtInitialThread() {
        if (hswThreadConnected != null) {
            setFlagError(true);
            notificationError();
            setHSWThreadConnected(null);
            stopConnection();
        }
    }

    public void notificationChangeService() {
        String title;
        String content;
        if (isFlagReconnection()) {
            title = String.format(getResources().getString(R.string.HSWatch_Notification_Title_Reconnection), this.deviceName);
            content = getResources().getString(R.string.HSWatch_Notification_Content_Reconnection);
        } else {
            title = String.format(
                    getResources().getString(R.string.HSWatch_Notification_Title_Connection_Started),
                    this.deviceName
            );
            content = getResources()
                    .getString(R.string.HSWatch_Notification_Content_Connection_Started);
        }
        this.notificationManager.notify(
                HSWFlag.FOREGROUND_ID,
                createForegroundNotification(title, content)
        );
    }

    public void connectionStablishConfirm() {
        sendActionInBroadcastReceiver(HSWFlag.HSW_CONNECTION_STATUS, false);
    }

    public void testConnection() {
        if (this.hswThreadTestConnection == null) {
            this.hswThreadTestConnection = new HSWThreadTestConnection(this);
            this.hswThreadTestConnection.start();
        }
    }

    public boolean isConnected() {
        return getConnectionStatus().getCurrentStatus() == HSWStatusFlag.STATE_CONNECTED;
    }

    public void threadInterrupted(Thread thread) {
        if (thread instanceof HSWThreadConnected) {
            ((HSWThreadConnected) thread).cancel();
        } else if (thread instanceof HSWThreadTestConnection) {
            sendActionInBroadcastReceiver(HSWFlag.HSW_KEEP_CONECTION, false);
        }
        stopConnection();
    }

    public void connectionLost() {
        if (hswThreadConnected != null) {
            setFlagError(true);
            setConnectionStatus(HSWStatusFlag.OUT_OF_RANGE);
            notificationErrorReconnection();
            hswThreadConnected.cancel();
            setHSWThreadConnected(null);
            setHSWThreadTestConnection(null);
            setHSWReconnectionThread();
        }
    }

    private void setHSWReconnectionThread() {
        if (!keepConnection) {
            setFlagReconnection(false);
            stopConnection();
        } else {
            setFlagReconnection(true);
            notificationChangeService();
            setConnectionStatus(HSWStatusFlag.RECONNECTING);
            hswThreadReconnection = new HSWThreadReconnection(this);
            hswThreadReconnection.start();
        }
    }

    private void notificationErrorReconnection() {
        String content = getResources().getString(R.string.HSWatch_Notification_Content_Error);
        if (this.keepConnection) {
            content += "";
            content += getResources().getString(R.string.HSWatch_Notification_Content_Reconnecting);
        }
        createNotification(
                getResources().getString(
                        R.string.HSWatch_Notification_Title_Error
                ),
                content
        );
    }

    public void setHSWThreadConnected(HSWThreadConnected hswThreadConnected) {
        HSWService.hswThreadConnected = hswThreadConnected;
    }

    public void setHSWThreadTestConnection(HSWThreadTestConnection hswThreadTestConnection) {
        this.hswThreadTestConnection = hswThreadTestConnection;
    }

    public boolean testWrittingConnection() throws IOException {
        if (hswThreadConnected != null)
            hswThreadConnected.write(HSWThreadConnected.delimitador);

        return hswThreadConnected != null;
    }

    public Context getCurrentContext() {
        return getApplicationContext();
    }

    public static void sendTime() {
        if (hswThreadConnected != null) hswThreadConnected.sendTime();
    }


    private void bluetoothIsOFF() {
        setFlagReconnection(false);
        createNotification(
                getResources().getString(R.string.HSWatch_Notification_Title_BTOFF),
                getResources().getString(R.string.HSWatch_Notification_Content_BTOFF)
        );
        cancelThreads();
        stopConnection();

    }

    /**
     * The method call when the service is about to end or is certain to end at this code's point.
     * It cancels all the possible threads that is still running.
     */
    private void cancelThreads() {
        if (hswThreadConnection != null) {
            setHSWThreadConnection(null);
        }
        if (hswThreadConnected != null) {
            hswThreadConnected.cancel();
            setHSWThreadConnected(null);
        }
        if (hswThreadReconnection != null) {
            setHSWThreadReconnection(null);
        }
        if (hswThreadTestConnection != null) {
            setHSWThreadTestConnection(null);
        }
    }

    public boolean isKeepConnection() {
        return keepConnection;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setHSWThreadReconnection(HSWThreadReconnection hswThreadReconnection) {
        this.hswThreadReconnection = hswThreadReconnection;
    }
}
