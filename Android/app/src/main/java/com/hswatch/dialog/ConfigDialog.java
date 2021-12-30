package com.hswatch.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.hswatch.R;

public class ConfigDialog extends DialogFragment {

    private final String title, content;
    private final ConfigOptions configOptions;


    public interface ConfigOptions {
        void positiveButton(String key, ConfigDialog configDialog);
        void negativeButton(ConfigDialog configDialog);
    }

    public ConfigDialog(String title, String content, ConfigOptions configOptions) {
        this.title = title;
        this.content = content;
        this.configOptions = configOptions;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            View view = View.inflate(getContext(), R.layout.dialog_config, null);
            builder.setView(view);
            if (view != null) {
                startUI(view);
            }

            Dialog dialog = builder.create();
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }

        return super.onCreateDialog(savedInstanceState);
    }

    private void startUI(View view) {
        ((TextView) view.findViewById(R.id.txt_config_dialog_title)).setText(title);
        TextView txtContent = view.findViewById(R.id.txt_config_dialog_content);
        txtContent.setMovementMethod(LinkMovementMethod.getInstance());
        txtContent.setText(content);
        ((TextView) view.findViewById(R.id.txt_config_dialog_content)).setText(content);
        view.findViewById(R.id.btn_config_dialog_positive).setOnClickListener(v -> {
            configOptions.positiveButton(
                    ((EditText) view.findViewById(R.id.edittxt_config_dialog)).getText().toString(),
                    this
            );
        });
        view.findViewById(R.id.btn_config_dialog_negative).setOnClickListener(v -> {
            configOptions.negativeButton(this);
        });
    }
}
