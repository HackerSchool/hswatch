package com.hswatch.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hswatch.MainActivity;
import com.hswatch.R;

import java.util.Objects;

public class finalizador_fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.finalizador_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.finalizadorBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((atividade_config) Objects.requireNonNull(getActivity())).guardarDispositivo();
                startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().finish();
            }
        });
    }

}
