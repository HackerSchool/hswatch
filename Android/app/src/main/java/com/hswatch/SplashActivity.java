package com.hswatch;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.hswatch.bluetooth.MainServico;
import com.hswatch.bluetooth.NoBTActivity;

//TODO(documentar)
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        verifyStateConnection();

    }

    private void verifyStateConnection() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        // First time open the app
        if (sharedPreferences.getBoolean(Utils.FIRST_START, true) ||
                MainServico.getCurrentState() == MainServico.NULL_STATE) {
            firstVerification();
        // If it's a connection going on, pass to the mainActivity's mainfragment
        } else if (MainServico.getCurrentState() == MainServico.STATE_CONNECTED ||
                MainServico.isFlagReconnection()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(Utils.MAIN_ACTIVITY_MODE, Utils.MAIN_ACTIVITY_CONNECTION);
            startActivity(intent);
        }
    }

    /**
     * Check if the telephone has the Bluetooth Radio in the system. If not, then the app
     * leaves with a toast message warning the user about the missing Bluetooth Radio.
     */
    private void firstVerification() {
        if (BluetoothAdapter.getDefaultAdapter() == null) {
            Toast.makeText(this, getResources().getText(R.string.BT_Unavailable), Toast.LENGTH_LONG).show();
            finish();
        } else {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                    Utils.BT_REQUEST);
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
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            if (sharedPreferences.getBoolean(Utils.FIRST_START, true)) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(Utils.MAIN_ACTIVITY_MODE, Utils.MAIN_ACTIVITY_FIRST_START);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(Utils.MAIN_ACTIVITY_MODE, Utils.MAIN_ACTIVITY_NEEDS_CONNECTION);
                startActivity(intent);
            }
            finish();
        }
    }
}
