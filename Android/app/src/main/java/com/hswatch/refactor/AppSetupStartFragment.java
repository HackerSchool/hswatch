package com.hswatch.refactor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hswatch.R;
import com.hswatch.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AppSetupStartFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_setup_start, container, false);

        ConfigurationFragment configurationFragment = (ConfigurationFragment) getParentFragment();

        if (configurationFragment != null) {
            configurationFragment.setBackgroundColorMode(Utils.BACKGROUND_APP);
            view.findViewById(R.id.btn_setup_app_start).setOnClickListener(v ->
                    configurationFragment.changeFragment(Utils.NEXT_FROM_APP_START));
        }


        return view;
    }
}
