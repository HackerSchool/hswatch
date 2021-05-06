package com.hswatch.refactor;

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
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.hswatch.MainActivity;
import com.hswatch.R;
import com.hswatch.SplashActivity;

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

    private String deviceName;

    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;

    private ThreadConnection threadConnection;
    private static ThreadConnected threadConnected;

    private BroadcastReceiverMainServico broadcastReceiverMainServico;

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

        //TODO(adicionar broadcast receiver para saber da ligação bt: bt on e off e se está fora de
        // alcance: https://developer.android.com/reference/android/bluetooth/BluetoothAdapter.html#ACTION_CONNECTION_STATE_CHANGED)

        //TODO(fechar o serviço na ativiade: https://stackoverflow.com/questions/20857120/what-is-the-proper-way-to-stop-a-service-running-as-foreground/20857343#20857343)

        createConection(this.bluetoothDevice);

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

    private void createConection(BluetoothDevice bluetoothDevice) {
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
        if (!this.flagError) {
            if (this.threadConnection != null) {
                setThreadConnection(null);
            }

            threadConnected = new ThreadConnected(this);
            threadConnected.start();
        }
    }

    public void connectionLostAtInitialThread() {
        if (threadConnected != null) {
            flagError = true;
            notificationError();
            setThreadConnected(null);
            setCurrentState(NULL_STATE);
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
            stopForeground(true);
            stopSelf();
        }
    }

    public void connectionLost() {
        if (threadConnected != null) {
            flagError = true;
            notificationError();
            threadConnected.cancel();
            setThreadConnected(null);
            setCurrentState(NULL_STATE);
            stopForeground(true);
            stopSelf();
        }
    }

    private void notificationError() {
        createNotification(getResources().getString(R.string.ServiceBT_BT_Error_Title),
                getResources().getString(R.string.ServiceBT_BT_Error_ContextText));
    }
    //endregion

    public class BroadcastReceiverMainServico extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            final String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    // Stop MainServico and ThreadConnected in case the Bluetooth is turn off
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                                == BluetoothAdapter.STATE_OFF) {
                            createNotification(
                                    getResources().getString(R.string.ServiceBT_BT_Off_Title),
                                    getResources().getString(R.string.ServiceBT_BT_Off_ContextText)
                            );
                            connectionLost();
                        }
                        break;
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

    public static void sendNotification(List<String> message) {
        if (threadConnected != null) {
            threadConnected.sendNotification(message);
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

    public static void setThreadConnected(ThreadConnected threadConnected) {
        MainServico.threadConnected = threadConnected;
    }
    //endregion
}
