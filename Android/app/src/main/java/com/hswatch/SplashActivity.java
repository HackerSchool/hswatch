package com.hswatch;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hswatch.bluetooth.NoBTActivity;
import com.hswatch.fragments.ConfigDeviceActivity;

import static com.hswatch.Utils.CHECKER;
import static com.hswatch.Utils.HISTORY_SHARED_PREFERENCES;
import static com.hswatch.Utils.NAME;

public class SplashActivity extends AppCompatActivity {

    // TODO acabar documentação nesta atividade
    //TODO(adicionar atividade caso esteja a fazer reconexão)

    public static final String TAG = "hswatch.ativ.splash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        startActivity(new Intent(this, MainActivity.class));
        finish();

        //TODO(criar um extra para iniciar a mainactivity num fragment especifico)

//        if (MainServico.isFlagInstante()) {
//            startActivity(new Intent(SplashActivity.this, MainActivity.class));
//            finish();
//        } else {
//            firstVerification();
//            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), Utils.BT_REQUEST);
//        }
    }

    /**
     * Check if the telephone has the Bluetooth Radio in the system. If not, then the app leaves with
     * a toast message warning the user about the missing Bluetooth Radio.
     */
    private void firstVerification() {
        if (BluetoothAdapter.getDefaultAdapter() == null){
            Toast.makeText(this, getResources().getText(R.string.BT_Unavailable), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.BT_REQUEST && resultCode == RESULT_CANCELED){
            startActivity(new Intent(SplashActivity.this, NoBTActivity.class));
            Toast.makeText(SplashActivity.this, getResources().getText(R.string.NoBT_Toast),
                    Toast.LENGTH_LONG).show();
            finish();
        } else if (requestCode == Utils.BT_REQUEST && resultCode == RESULT_OK) {
            verifyHistoryDevice();
            finish();
        }
    }

    private void verifyHistoryDevice() {
        SharedPreferences sharedPreferences = getSharedPreferences(HISTORY_SHARED_PREFERENCES, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(CHECKER, false)) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.BT_Device_Conected) +
                    sharedPreferences.getString(NAME, "Erro"), Toast.LENGTH_LONG).show();
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        } else {
            startActivity(new Intent(SplashActivity.this, ConfigDeviceActivity.class));
            finish();
        }
    }
}
