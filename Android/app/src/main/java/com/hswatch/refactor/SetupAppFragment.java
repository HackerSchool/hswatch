package com.hswatch.refactor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hswatch.R;
import com.hswatch.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SetupAppFragment extends Fragment {

    private TextView txtViewTitle;
    private TextView txtViewContent;
    private ImageView imgViewIcon;
    private Button btnNext;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup_app, container, false);

        this.txtViewTitle = view.findViewById(R.id.txtview_title_setup_app);
        this.txtViewContent = view.findViewById(R.id.txtview_content_setup_app);
        this.imgViewIcon = view.findViewById(R.id.imgview_setup_app);
        this.btnNext = view.findViewById(R.id.btn_setup_app);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null) {
            this.txtViewTitle.setText(bundle.getString(Utils.SETUP_APP_TITLE,
                    getResources().getString(R.string.setup_start_app_title)));
            this.txtViewContent.setText(bundle.getString(Utils.SETUP_APP_CONTENT,
                    getResources().getString(R.string.setup_start_app_content)));
            this.imgViewIcon.setContentDescription(bundle
                    .getString(Utils.SETUP_APP_CONTENT_DESCRIPTION,
                    getResources().getString(R.string.setup_start_app_image_description)));
            this.imgViewIcon.setImageResource(bundle.getInt(Utils.SETUP_APP_IMAGE_RESOURCE,
                    R.drawable.hswatch_icon));
            this.btnNext.setText(bundle.getString(Utils.SETUP_APP_BUTTON_TEXT,
                    getResources().getString(R.string.setup_start_app_btn)));
            ConfigurationFragment configurationFragment = (ConfigurationFragment) getParentFragment();
            if (configurationFragment != null) {
                this.btnNext.setOnClickListener(v ->
                        configurationFragment.changeFragment(bundle.getInt(Utils.SETUP_APP_STATUS,
                                configurationFragment.getChildFragmentManager()
                                        .getBackStackEntryCount() + 1)));
                configurationFragment.setBackgroundColorMode(bundle.getInt(Utils.SETUP_APP_BACKGROUNDMODE, Utils.BACKGROUND_CONNECTION));
            }
        } else {
            this.txtViewTitle.setText(getResources().getString(R.string.setup_start_app_title));
            this.txtViewContent.setText(getResources().getString(R.string.setup_start_app_content));
            this.imgViewIcon.setContentDescription(getResources()
                    .getString(R.string.setup_start_app_image_description));
            this.imgViewIcon.setImageResource(R.drawable.hswatch_icon);
            this.btnNext.setText(getResources().getString(R.string.setup_start_app_btn));
            ConfigurationFragment configurationFragment = (ConfigurationFragment) getParentFragment();
            if (configurationFragment != null) {
                this.btnNext.setOnClickListener(v ->
                        configurationFragment.changeFragment(configurationFragment
                                .getChildFragmentManager().getBackStackEntryCount() + 1));
                configurationFragment.setBackgroundColorMode(Utils.BACKGROUND_CONNECTION);
            }

        }

    }
}
