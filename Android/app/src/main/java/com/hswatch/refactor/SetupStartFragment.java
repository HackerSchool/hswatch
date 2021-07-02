package com.hswatch.refactor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hswatch.R;
import com.hswatch.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SetupStartFragment extends Fragment {

    //TODO(verificar bluetooth connection aqui ou no configuration)

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.fragment_setup_start,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_setup_start).setOnClickListener(v -> {
            ConfigurationFragment configurationFragment = (ConfigurationFragment) getParentFragment();

            if (configurationFragment != null) {
                configurationFragment.changeFragment(Utils.NEXT_FROM_START);
            }
        });
    }
}
