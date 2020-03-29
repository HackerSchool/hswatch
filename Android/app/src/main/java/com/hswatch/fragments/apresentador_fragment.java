package com.hswatch.fragments;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hswatch.R;
import com.hswatch.atividade_config;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

public class apresentador_fragment extends Fragment {

//    TAG
    public static final String TAG = "hswatch.fragment.apres";

//    Variáveis globais
    private boolean verificador = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.apresentador_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button seguidor = view.findViewById(R.id.apresentadorBtn);
        seguidor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificador) {
                    ((atividade_config) Objects.requireNonNull(getActivity())).seguir_fragment();
                    verificador = false;
                } else {
                    final Intent intBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intBT, getResources().getInteger(R.integer.ATIVAR_BT));
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == getResources().getInteger(R.integer.ATIVAR_BT) && resultCode == RESULT_OK){
            verificador = true;
            ((atividade_config) Objects.requireNonNull(getActivity())).seguir_fragment();
        }
    }

}
