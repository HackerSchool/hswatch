package com.hswatch.refactor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hswatch.MainActivity;
import com.hswatch.R;
import com.hswatch.Utils;
import com.hswatch.paginaPrincipal;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class ConfigurationFragment extends Fragment {

    private MainActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_configuration_master,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.mainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Bundle bundle = getArguments();

        if (bundle != null) {
            if (bundle.getBoolean(Utils.CONFIGURATION_MODE)) {
                getChildFragmentManager().beginTransaction().replace(R.id.frame_message,
                        new AppSetupStartFragment()).commit();
            } else {
                SetupAppFragment setupAppFragment = new SetupAppFragment();

                Bundle fragmentMessage = new Bundle();
                fragmentMessage.putString(Utils.SETUP_APP_TITLE, getResources()
                        .getString(R.string.setup_start_title));
                fragmentMessage.putString(Utils.SETUP_APP_CONTENT, getResources()
                        .getString(R.string.setup_start_content));
                fragmentMessage.putString(Utils.SETUP_APP_CONTENT_DESCRIPTION, getResources()
                        .getString(R.string.setup_app_image_description));
                fragmentMessage.putString(Utils.SETUP_APP_BUTTON_TEXT, getResources()
                        .getString(R.string.setup_start_btn));
                fragmentMessage.putInt(Utils.SETUP_APP_IMAGE_RESOURCE, R.drawable.bt_symbol);
                fragmentMessage.putInt(Utils.SETUP_APP_STATUS, Utils.NEXT_FROM_START);
                fragmentMessage.putInt(Utils.SETUP_APP_BACKGROUNDMODE, Utils.BACKGROUND_CONNECTION);
                setupAppFragment.setArguments(fragmentMessage);

                getChildFragmentManager().beginTransaction()
                        .replace(R.id.frame_message, setupAppFragment)
                        .commit();
            }
        }
    }

    public void changeFragment(int fragmentChosen) {
        switch (fragmentChosen) {
            case Utils.NEXT_FROM_APP_START:
                makeTransiction(Utils.INITIAL_APP_STATE_TAG_FRAGMENT,
                        getResources().getString(R.string.setup_func_app_title),
                        getResources().getString(R.string.setup_func_app_content),
                        getResources().getString(R.string.setup_func_app_btn),
                        R.drawable.bt_symbol_dot,
                        Utils.NEXT_FROM_APP_FUNC,
                        Utils.BACKGROUND_APP,
                        getResources().getString(R.string.setup_func_app_image_description)
                );
                break;
            case Utils.NEXT_FROM_APP_FUNC:
                makeTransiction(Utils.FUNCTIONALITY_APP_STATE_TAG_FRAGMENT,
                        getResources().getString(R.string.setup_pair_app_title),
                        getResources().getString(R.string.setup_pair_app_content),
                        getResources().getString(R.string.setup_pair_app_btn),
                        R.drawable.bt_symbol_dot_weak,
                        Utils.NEXT_FROM_APP_PAIR,
                        Utils.BACKGROUND_APP,
                        getResources().getString(R.string.setup_pair_app_image_description)
                );
                break;
            case Utils.NEXT_FROM_APP_PAIR:
                makeTransiction(Utils.PAIR_APP_STATE_TAG_FRAGMENT,
                        getResources().getString(R.string.setup_background_service_app_title),
                        getResources().getString(R.string.setup_background_service_app_content),
                        getResources().getString(R.string.setup_pair_app_btn),
                        R.drawable.bt_symbol_dot_medium,
                        Utils.NEXT_FROM_APP_BACKGROUND_SERVICE,
                        Utils.BACKGROUND_APP,
                        getResources().getString(R.string.setup_background_service_app_image_description)
                );
                break;
            case Utils.NEXT_FROM_APP_BACKGROUND_SERVICE:
                makeTransiction(Utils.BACKGROUND_SERVICE_APP_STATE_TAG_FRAGMENT,
                        getResources().getString(R.string.setup_finished_app_title),
                        getResources().getString(R.string.setup_finished_app_content),
                        getResources().getString(R.string.setup_finished_app_btn),
                        R.drawable.bt_symbol_dot_strong,
                        Utils.INITIAL_STATE,
                        Utils.BACKGROUND_APP,
                        getResources().getString(R.string.setup_finished_app_image_description)
                );
                break;
            // Transition from the Informative Fragment to the Setup Fragment
            case Utils.INITIAL_STATE:
                makeTransiction(Utils.INITIAL_STATE_TAG_FRAGMENT,
                        getResources().getString(R.string.setup_start_title),
                        getResources().getString(R.string.setup_start_content),
                        getResources().getString(R.string.setup_start_btn),
                        R.drawable.bt_symbol,
                        Utils.NEXT_FROM_START,
                        Utils.BACKGROUND_CONNECTION
                );
                if (this.mainActivity != null) {
                    SharedPreferences.Editor editor = this.mainActivity
                            .getSharedPreferences(Utils.HISTORY_SHARED_PREFERENCES,
                                    Context.MODE_PRIVATE).edit();
                    editor.putBoolean(Utils.FIRST_START, false);
                    editor.apply();
                }
                break;

            // Transition from the Setup Fragment to the Service Fragment
            case Utils.NEXT_FROM_START:
                makeTransiction(new SetupServiceFragment(),
                        Utils.SETUP_TAG_FRAGMENT);
                break;

            // Transition from the Service Fragment to the Finishing Fragment
            case Utils.NEXT_FROM_LIST:
                makeTransiction(Utils.FINISHING_TAG_FRAGMENT,
                        getResources().getString(R.string.setup_finishing_title),
                        getResources().getString(R.string.setup_finishing_content),
                        getResources().getString(R.string.setup_finishing_btn),
                        R.drawable.ic_baseline_check_circle_outline,
                        Utils.NEXT_FROM_FINISH,
                        Utils.BACKGROUND_CONNECTION
                );
                break;

            case Utils.NEXT_FROM_FINISH:
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frame, new paginaPrincipal())
                        .commit();
                if (this.mainActivity != null) {
                    this.mainActivity.verifyNotificationsSetup();
                }
                break;
            default:break;
        }
    }

    private void makeTransiction(@NonNull androidx.fragment.app.Fragment fragment, String tag) {
        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right,
                        R.animator.slide_in_pop_left, R.animator.slide_out_pop_right)
                .replace(R.id.frame_message, fragment)
                .addToBackStack(tag)
                .commit();
    }

    private void makeTransiction(@NonNull String tag, @NonNull String title,
                                 @NonNull String content, @NonNull String btnText,
                                 @DrawableRes int imageResource,
                                 int status, int backgroundMode) {
        makeTransiction(tag, title, content, btnText, imageResource, status, backgroundMode,
                getResources().getString(R.string.setup_app_image_description));
    }

    private void makeTransiction(@NonNull String tag, @NonNull String title,
                                 @NonNull String content, @NonNull String btnText,
                                 @DrawableRes int imageResource,
                                 int status, int backgroundMode,
                                 @NonNull String contentDescription) {

        SetupAppFragment setupAppFragment = new SetupAppFragment();

        Bundle bundle = new Bundle();
        bundle.putString(Utils.SETUP_APP_TITLE, title);
        bundle.putString(Utils.SETUP_APP_CONTENT, content);
        bundle.putString(Utils.SETUP_APP_CONTENT_DESCRIPTION, contentDescription);
        bundle.putString(Utils.SETUP_APP_BUTTON_TEXT, btnText);
        bundle.putInt(Utils.SETUP_APP_IMAGE_RESOURCE, imageResource);
        bundle.putInt(Utils.SETUP_APP_STATUS, status);
        bundle.putInt(Utils.SETUP_APP_BACKGROUNDMODE, backgroundMode);
        setupAppFragment.setArguments(bundle);

        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right,
                        R.animator.slide_in_pop_left, R.animator.slide_out_pop_right)
                .replace(R.id.frame_message, setupAppFragment)
                .addToBackStack(tag)
                .commit();
    }

    @NonNull
    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setBackgroundColorMode(int backgroundColor) {
        if (mainActivity != null) {
            View view = getView();
            if (view != null) {
                view.setBackground(ContextCompat.getDrawable(mainActivity
                        .getApplicationContext(), (1- backgroundColor) * R.color.HS_Circ_4 +
                        backgroundColor * R.color.HS_Circ_3));
            }
        }
    }
}
