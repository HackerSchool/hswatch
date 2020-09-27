package com.hswatch.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.hswatch.R;

public class sem_bt extends AppCompatActivity {

    public static final String TAG = "hswatch.atividade.sembt";

    @SuppressLint("ClickableViewAccessibility")  //criar forma para os cegos
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sem_bt);

        RelativeLayout relativeLayout = findViewById(R.id.semBTatividade);
        relativeLayout.setOnTouchListener(onTouchListener);
    }

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN && BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                v.performClick();
//                Log.v(TAG, "Clicki" + "\t" + (event.getAction() == MotionEvent.ACTION_DOWN) + "\t" + BluetoothAdapter.getDefaultAdapter().isEnabled());
                finish();
                return true;
            }
            return false;
        }
    };
}
