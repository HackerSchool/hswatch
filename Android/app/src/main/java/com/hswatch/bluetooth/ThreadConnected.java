package com.hswatch.bluetooth;

import android.bluetooth.BluetoothSocket;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.hswatch.Utils;
import com.hswatch.worker.HoraWorker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.hswatch.Utils.TIME_INDICATOR;
import static com.hswatch.Utils.WEATHER_INDICATOR;
import static com.hswatch.Utils.connectionSucceeded;
import static com.hswatch.Utils.delimitador;
import static com.hswatch.Utils.separador;
import static com.hswatch.Utils.tryConnecting;

//TODO(documentar)
public class ThreadConnected extends Thread {

    private static final String TAG = "ThreadConnected_Funciona_TAG";

    /**
     * Bluetooth Socket where the device is connected to. It could be null if not handled properly
     */
    private final BluetoothSocket bluetoothSocket;

    /**
     * InputStream variable which is capable to retrieve data received from the connected
     * Bluetooth Device
     */
    private final InputStream inputStream;

    /**
     * OutputStream variable which is capable to send data from the application to connected the
     * Bluetooth Device
      */
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
     * Current buffer position to write on the #this.charByteReceived variable
     */
    private int bufferPosition = 0;

    /**
     * Array of char created from the bytes received in the connection. This array is used to
     * generate the message received in string. It has a size of 3 elements.
     */
    private char[] charByteReceived = new char[3];

    /**
     * Message received from the readings made in the connection. It can be used to handle requests
     * from the Bluetooth Device connected.
     */
    private String messageReceived = "";

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

        // Get the input and output streams to be used through out the connection
        this.inputStream = getInputStream(this.bluetoothSocket);
        this.outputStream = getOutputStream(this.bluetoothSocket);

        // Updates the connection state on the Service
        mainServico.setCurrentState(MainServico.STATE_CONNECTED);

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
     * Retrieves the Bluetooth Device's InputStream, while handle the error that may appears in
     * the operation.
     *
     * @param bluetoothSocket The Bluetooth Device's socket which can retrieve the InputStream
     *                        wanted.
     * @return The InputStream wanted. Can be null in case there was an error
     */
    private InputStream getInputStream(BluetoothSocket bluetoothSocket) {
        InputStream inputStream = null;
        try {
            inputStream = bluetoothSocket.getInputStream();
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
            this.mainServico.connectionLostAtInitialThread();
        }
        return inputStream;
    }

    /**
     * Retrieves the Bluetooth Device's OutputStream, while handle the error that may appears in
     * the operation.
     *
     * @param bluetoothSocket The Bluetooth Device's socket which can retrieve the OutputStream
     *                        wanted.
     * @return The OutputStream wanted. Can be null in case there was an error
     */
    private OutputStream getOutputStream(BluetoothSocket bluetoothSocket) {
        OutputStream outputStream = null;
        try {
            outputStream = bluetoothSocket.getOutputStream();
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
            this.mainServico.connectionLostAtInitialThread();
        }
        return outputStream;
    }

    /**
     * The main cycle of the connection, where it reads the data sent by the Bluetooth Device
     */
    @Override
    public void run() {
        // Send the first data to the Bluetooth Device: update the time that is shown on the watch
        sendTime();

        // Then tell to ThreadReconnection to stop
        this.mainServico.setFlagReconnection(false);

        // If it's a reconnection, change the foreground notification text and cancel the
        // ReconnectionThread
        this.mainServico.notificationChangeService();

        // Tells to the service that exists a connection running, so the SplashActivity can start
        // the MainActivity instead the SetupActivity
        MainServico.setFlagInstante(true);
        tryConnecting = false;
        connectionSucceeded = true;

        // Initializes the test thread
        this.mainServico.testConnection();

        // While there is connection between the phone and the Bluetooth Device
        manageConnection();

    }

    private void manageConnection() {
        while (this.mainServico.isConnected() || this.bluetoothSocket.isConnected()) {
            try {
                // Verify if there is something to read
                int bytesAvailable = this.inputStream.available();
                if (bytesAvailable > 0) {

                    // Check if we're not at the end of the stream
                    // If yes, then get out of the loop
                    byte[] buffer = new byte[bytesAvailable];
                    if (this.inputStream.read(buffer) == -1) {
                        throw new IOException("InputStream got -1 while reading to the buffer!");
                    }

                    // Reads the data which was received through the connection byte per byte
                    readBytesReceived(bytesAvailable, buffer);

                    // Restart the scanning variables in case there was an indicator in the
                    // message received
                    if (interpretMessageReceived(messageReceived)) {
                        charByteReceived = new char[3];
                        messageReceived = "";
                    }
                }

                // Throws an error in case the current thread was interrupted
                if (Thread.interrupted()) {
                    throw new InterruptedException("This thread was interrupted!");
                }

            } catch (IOException e) {
                e.printStackTrace();
                this.mainServico.connectionLost();
                break;
            } catch (InterruptedException interruptedException) {
                this.mainServico.threadInterrupted(this);
                break;
            }
        }
    }

    /**
     * Read each byte on the buffer array and convert them to string in the messageReceived
     * variable. It stops the reading until it finds the delimiter byte or, if the bufferposition
     * variable is out of bonds, when it catches the Index Out Of Bounds Exception.
     *
     * @param bytesAvailable The amount of bytes available to read. It derives from the
     *                       #inputstream.available() method
     * @param buffer A byte array where is storaged the bytes to be read and convert to string. It
     *               should be initialize before the call of this method
     */
    private void readBytesReceived(int bytesAvailable, byte[] buffer) {
        // Interates for each byte in the buffer
        for (int byteIndex = 0; byteIndex < bytesAvailable; byteIndex++) {
            byte currentByte = buffer[byteIndex];

            // Check if is the end of the message. If not, add correspondent letter to
            // char array
            if (delimitador[0] == currentByte) {
                this.messageReceived = new String(this.charByteReceived);
                this.bufferPosition = 0;
            } else {
                try {
                    this.charByteReceived[this.bufferPosition++] = (char) currentByte;
                } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                    // In case the bufferPosition is a value out of boundaries to the
                    // charByteReceived, then end the scanning on the correspondent
                    // byte and form the message
                    this.messageReceived = new String(this.charByteReceived);
                    this.bufferPosition = 0;
                    break;
                }
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
        } catch (Exception e) {
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
        } catch (Exception e) {
            e.printStackTrace();
            this.mainServico.connectionLost();
        }
    }

    public void sendMessage(List<String> message) {
        try {
            this.write(message.get(0).getBytes());
            for (String msg : message.subList(1,message.size())) {
                this.write(separador);
                this.write(msg.getBytes());
            }
            this.write(delimitador);
        } catch (Exception e) {
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