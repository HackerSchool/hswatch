package com.hswatch.refactor;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.hswatch.Utils;
import com.hswatch.worker.HoraWorker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import static com.hswatch.Utils.TIME_INDICATOR;
import static com.hswatch.Utils.WEATHER_INDICATOR;
import static com.hswatch.Utils.delimitador;
import static com.hswatch.Utils.separador;

//TODO(documentar)
public class ThreadConnected extends Thread {

    private static final String TAG = "ThreadConnected_Funciona_TAG";

    /**
     * The variables to operate the Bluetooth device: Socket and the Input and Output streams
     */
    private final BluetoothSocket bluetoothSocket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    /**
     * The current Watch (Bluetooth device) connected in this thread.
     */
    private final Watch currentWatch;

    /**
     * Main Service on which the thread is started and ended on and also where the thread gets the
     * current state of the connection and to connect to the other Java API frameworks
     */
    private final MainServico mainServico;

    /**
     * The ThreadConnected's constructor in which starts the connection and initializes the Socket,
     * Input and Output streams of the connection and initializes the current HSWatch in which
     * interchange the data from and to the App.
     *
     * @param mainServico The main service where the connection will operate on and communicate with
     *                    the other Java API frameworks and Connector Threads
     */
    public ThreadConnected(MainServico mainServico) {
        this.bluetoothSocket = mainServico.getBluetoothSocket();
        this.mainServico = mainServico;

        // Initializes the stream variables
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = this.bluetoothSocket.getInputStream();
            outputStream = this.bluetoothSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            this.mainServico.connectionLostAtInitialThread();
        }
        this.inputStream = inputStream;
        this.outputStream = outputStream;

        // Updates the connection state on the Service
        mainServico.setCurrentState(MainServico.STATE_CONNECTED);

        //TODO(green signal with name)

        // Initializes the Watch object
        this.currentWatch = new Watch(mainServico.getCurrentContext(),
                mainServico.getBluetoothDevice());

        // Initializes the time update service with Schedule Jobs:
        // It updates time with the value from the settings, in minutes, but as 15 minutes as
        // default value
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(HoraWorker.class,
                this.currentWatch.getTimeInterval(), TimeUnit.MINUTES).build();
        WorkManager.getInstance(mainServico.getCurrentContext()).enqueueUniquePeriodicWork(
                Utils.TAG_HOURS, ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest
        );
    }

    /**
     * The main cycle of the connection, where it reads the data sent by the Bluetooth Device
     */
    @Override
    public void run() {
        // To store the bytes received through the connection and then to read them with the string
        // messageReceived
        int bufferPosition = 0;
        char[] charByteReceived = new char[3];
        String messageReceived = "";

        // Send the first data to the Bluetooth Device: update the time that is shown on the watch
        sendTime();

        // While there is connection between the phone and the Bluetooth Device
        while (this.mainServico.isConnected() || this.bluetoothSocket.isConnected()) {
            try {
                // Verify if there is something to read
                int bytesAvailable = this.inputStream.available();
                if (bytesAvailable > 0) {

                    // Check if we're not at the end of the stream
                    // If yes, the get out of the loop
                    byte[] buffer = new byte[bytesAvailable];
                    if (this.inputStream.read(buffer) == -1) {
                        //TODO(ter efeito sobre o que acontece no final do inputstream read)
                        break;
                    }

                    // Reads the data which was received through the connection byte per byte
                    for (int byteIndex = 0; byteIndex < bytesAvailable; byteIndex++) {
                        byte currentByte = buffer[byteIndex];

                        // Check if is the end of the message. If not, add correspondent letter to
                        // char array
                        if (delimitador[0] == currentByte) {
                            messageReceived = new String(charByteReceived);
                            bufferPosition = 0;
                        } else {
                            try {
                                charByteReceived[bufferPosition++] = (char) currentByte;
                            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                                // In case the bufferPosition be a value out of boundaries to the
                                // charByteReceived, then end the scanning on the correspondent
                                // byte and form the message
                                messageReceived = new String(charByteReceived);
                                bufferPosition = 0;
                                break;
                            }
                        }
                    }

                    // Restart the scanning variables in case there was an indicator in the
                    // message received
                    if (interpretMessageReceived(messageReceived)) {
                        charByteReceived = new char[3];
                        messageReceived = "";
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                this.mainServico.connectionLost();
                break;
            }
        }
    }

    /**
     * Interprets the messageReceived to find some indicator pre-established by the programmer.
     * Currently, there is only one indicator: WEATHER_INDICATOR = "WEA" - in which tell to the
     * app to send the current weather status and for the next 6 days - but the idea here is to
     * use the switch for more indicators which can be added later on the project.
     *
     * @param messageReceived The message received through the Bluetooth Connection and to be
     *                        verified.
     * @return A true or false variable in which tells if there was an indicator or not in the
     * message given
     */
    private boolean interpretMessageReceived(@NonNull String messageReceived) {
        switch (messageReceived) {
            case WEATHER_INDICATOR:
                this.currentWatch.getWeatherStatus(this::sendWeatherStatus);
                return true;
            default:
                return false;
        }
    }

    /**
     * Write the weather status received from the API to the Bluetooth device through the request
     * was received.
     *
     * @param requestList The list with weather status in strings to be sent to the bluetooth Device
     */
    private void sendWeatherStatus(@NonNull ArrayList<String> requestList) {
        try {
            this.write(WEATHER_INDICATOR.getBytes());
            for (String element : requestList) {
                this.write(separador);
                this.write(element.getBytes());
            }
            this.write(delimitador);
        } catch (IOException e) {
            e.printStackTrace();
            this.mainServico.connectionLost();
        }
    }

    /**
     * Send the current time of the phone to the Bluetooth Device
     */
    public void sendTime() {
        String[] timeMessage = this.currentWatch.getCurrentTime();
        try {
            this.write(TIME_INDICATOR.getBytes());
            for (String msg : timeMessage) {
                this.write(separador);
                this.write(msg.getBytes());
            }
            this.write(delimitador);
        } catch (IOException e) {
            e.printStackTrace();
            this.mainServico.connectionLost();
        }
    }

    /**
     * This function send data to the bluetooth device in form of a byte array.
     *
     * @param buffer The data in byte array to be sent via Bluetooth connection
     * @throws IOException An input-output error that can occur while sending data to the Bluetooth
     * Device
     */
    public void write(byte[] buffer) throws IOException {
        //TODO(descobrir porque o socket está fechado)
        Log.d(TAG, "write() called with: buffer = [" + Arrays.toString(buffer) + "]\n" +
                this.bluetoothSocket.toString());
        this.outputStream.write(buffer);
    }

    /**
     * Cancel the current Bluetooth connection
     */
    public void cancel() {
        try {
            this.bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}