package com.hswatch.bluetooth;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hswatch.R;

/**
 * Activity when the user denys turn on the BT.
 * It finishes when the user press the Back Button
 */
public class NoBTActivity extends AppCompatActivity {

    public static final String TAG = "hswatch.atividade.sembt";

    //TODO acrescentar aviso em baixo para ativar de novo o BT
    //TODO acabar documentação

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sem_bt);

    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
