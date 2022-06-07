package io.hackerschool.hswatch_connection_module.connection_threads;

import android.bluetooth.BluetoothSocket;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.hackerschool.hswatch_connection_module.HSWService;
import io.hackerschool.hswatch_connection_module.connection_objects.HSWConnection;
import io.hackerschool.hswatch_connection_module.connection_objects.IHSWProtocolSender;
import io.hackerschool.hswatch_connection_module.flags.HSWFlag;
import io.hackerschool.hswatch_connection_module.flags.HSWStatusFlag;

public class HSWThreadConnected extends Thread {
    /**
     *
     */
    private final BluetoothSocket bluetoothSocket;
    /**
     *
     */
    private final HSWService hswService;
    /**
     *
     */
    private final InputStream inputStream;
    /**
     *
     */
    private final OutputStream outputStream;
    /**
     *
     */
    private String messageReceived = "";
    /**
     *
     */
    private char[] charByteReceived = new char[3];

    // Protocol's keys
    /**
     *
     */
    public static final byte[] separador = {0x03};
    /**
     *
     */
    public static final byte[] delimitador = {0x00};
    /**
     *
     */
    private int bufferPosition;
    /**
     *
     */
    private final HSWConnection currentConnection = new HSWConnection();

    public HSWThreadConnected(@NonNull HSWService hswService) {
        this.bluetoothSocket = hswService.getBluetoothSocket();
        this.hswService = hswService;

        this.inputStream = getInputStream(this.bluetoothSocket, this.hswService);
        this.outputStream = getOutputStream(this.bluetoothSocket, this.hswService);

        HSWService.setConnectionStatus(HSWStatusFlag.STATE_CONNECTED);

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(
                HSWHourWorker.class,
                15,
                TimeUnit.MINUTES
        ).build();
        WorkManager.getInstance(this.hswService.getCurrentContext()).enqueueUniquePeriodicWork(
                HSWFlag.TAG_HOURS,
                ExistingPeriodicWorkPolicy.REPLACE,
                periodicWorkRequest
        );
    }

    /**
     * Retrieves the Bluetooth Device's InputStream, while handle the error that may appears in
     * the operation.
     *
     * @param bluetoothSocket The Bluetooth Device's socket which can retrieve the InputStream
     *                        wanted.
     * @param hswService    the HSWatch service which is handling all the status and data for the
     *                      current connection that is trying to make connection from.
     * @return The InputStream wanted. Can be null in case there was an error
     */
    private InputStream getInputStream(BluetoothSocket bluetoothSocket, HSWService hswService) {
        InputStream inputStream = null;
        try {
            inputStream = bluetoothSocket.getInputStream();
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
            hswService.connectionLostAtInitialThread();
        }
        return inputStream;
    }

    /**
     * Retrieves the Bluetooth Device's OutputStream, while handle the error that may appears in
     * the operation.
     *
     * @param bluetoothSocket The Bluetooth Device's socket which can retrieve the OutputStream
     *                        wanted.
     * @param hswService    the HSWatch service which is handling all the status and data for the
     *                      current connection that is trying to make connection from.
     * @return The OutputStream wanted. Can be null in case there was an error
     */
    private OutputStream getOutputStream(BluetoothSocket bluetoothSocket, HSWService hswService) {
        OutputStream outputStream = null;
        try {
            outputStream = bluetoothSocket.getOutputStream();
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
            hswService.connectionLostAtInitialThread();
        }
        return outputStream;
    }

    @Override
    public void run() {
        sendTime();

//        this.hswService.setFlagReconnection(false);

        this.hswService.notificationChangeService();

        this.hswService.connectionStablishConfirm();

        this.hswService.testConnection();

        manageConnection();
    }

    /**
     * Send the current time of the phone to the Bluetooth Device
     */
    public void sendTime() {
        interpretMessageReceived(currentConnection.getIndicatorTime());
    }

    private void manageConnection() {
        while (this.hswService.isConnected()) {
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
                this.hswService.connectionLost();
                return;
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
                this.hswService.threadInterrupted(this);
                return;
            }
        }
        this.hswService.connectionLost();
    }

    private boolean interpretMessageReceived(String messageReceived) {
        Map<String, IHSWProtocolSender> mapProtocol = currentConnection.getProtocolMapSenders();
        if (mapProtocol.containsKey(messageReceived)) {
            sendListOfStrings(
                    messageReceived,
                    Objects.requireNonNull(
                            mapProtocol.get(messageReceived)
                        ).protocolSenderCallback()
            );
            return true;
        } else {
            return false;
        }
    }

    private void sendListOfStrings(@NonNull String messageReceived, @NonNull List<String> callbackResponse) {
        try {
            this.write(messageReceived.getBytes());
            for (String element : callbackResponse) {
                this.write(separador);
                this.write(element.getBytes());
            }
            this.write(delimitador);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            this.hswService.connectionLost();
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
