package com.hswatch.refactor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hswatch.MainActivity;
import com.hswatch.R;
import com.hswatch.Utils;
import com.hswatch.paginaPrincipal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ConfigurationFragment extends Fragment {

    private MainActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_configuration_master, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity = (MainActivity) getActivity();


        getChildFragmentManager().beginTransaction().replace(R.id.frame_message,
                new SetupStartFragment(), Utils.SETUP_TAG_FRAGMENT).commit();
    }

    public void changeFragment(int fragmentChosen) {
//        Toast.makeText(getContext(), "Now this, puts a smile on my face...", Toast.LENGTH_SHORT).show();

        //TODO(fragmento de iniciar a app pela primeira vez)

        switch (fragmentChosen) {
            // Transition from the Informative Fragment to the Setup Fragment
            case Utils.INITIAL_STATE:
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.frame_message, new SetupStartFragment())
                        .addToBackStack(Utils.INITIAL_STATE_TAG_FRAGMENT)
                        .commit();
                break;

            // Transition from the Setup Fragment to the Service Fragment
            //TODO(verificar o porquê de aparecer imensas notificações quando se desliga o serviço)
            //TODO(perceber porque é que o serviço termina quando se bloqueia o tlm)

            case Utils.NEXT_FROM_START:
                getChildFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right,
                                R.animator.slide_in_pop_left, R.animator.slide_out_pop_right)
                        .replace(R.id.frame_message, new SetupServiceFragment())
                        .addToBackStack(Utils.SETUP_TAG_FRAGMENT)
                        .commit();
                break;

            // Transition from the Service Fragment to the Finishing Fragment
            case Utils.NEXT_FROM_LIST:
                getChildFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right,
                                R.animator.slide_in_pop_left, R.animator.slide_out_pop_right)
                        .replace(R.id.frame_message, new FinishingSetupFragment())
                        .addToBackStack(Utils.FINISHING_TAG_FRAGMENT)
                        .commit();
                break;

            case Utils.NEXT_FROM_FINISH:
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frame, new paginaPrincipal())
                        .commit();
                break;
            default:break;
        }
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }
}
