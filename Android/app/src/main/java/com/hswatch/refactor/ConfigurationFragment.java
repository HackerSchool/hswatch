package com.hswatch.refactor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hswatch.R;
import com.hswatch.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ConfigurationFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_configuration_master, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getChildFragmentManager().beginTransaction().replace(R.id.frame_message,
                new SetupStartFragment()).commit();
    }

    public void changeFragment(int fragmentChosen) {
        Toast.makeText(getContext(), "Yes I will...", Toast.LENGTH_SHORT).show();

        //TODO(fragmento de iniciar a app pela primeira vez)

        switch (fragmentChosen) {
            case Utils.NEXT_FROM_START:
                getChildFragmentManager().beginTransaction().replace(R.id.frame_message,
                        new SetupServiceFragment()).commit();
                break;
            case Utils.NEXT_FROM_LIST:
                //TODO(iniciar fragment com a mensagem de que se ligou com sucesso)
                break;
            default:break;
        }

        //TODO(switch para mudar de fragment)
        //TODO(adicionar os restantes fragmentos e serviço para depois na sexta ter o layout melhorado acabado para o HackFeed)
    }
}
