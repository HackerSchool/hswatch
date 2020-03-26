package com.hswatch;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        verificacao_Inicial();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.atividade_principal);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_abrir, R.string.navigation_fechar);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //    private void verificacao_Inicial() {
//        BluetoothAdapter radioBT = BluetoothAdapter.getDefaultAdapter();
//        if (radioBT == null){
//            Toast.makeText(this, "Bluetooth Indisponível!", Toast.LENGTH_LONG).show();
//            finish();
//        }
//    }
}
