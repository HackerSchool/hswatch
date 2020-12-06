package com.hswatch.fragments;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hswatch.R;

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

        view.findViewById(R.id.apresentadorBtn).setOnClickListener(v -> {
            if (verificador) {
                ((atividade_config) requireActivity()).seguir_fragment();
                ((atividade_config) requireActivity()).listaBluetooth(true);
                verificador = false;
            } else {
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), getResources().getInteger(R.integer.ATIVAR_BT));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == getResources().getInteger(R.integer.ATIVAR_BT) && resultCode == RESULT_OK){
            verificador = true;
            ((atividade_config) requireActivity()).seguir_fragment();
            ((atividade_config) requireActivity()).listaBluetooth(true);
        }
    }

}
