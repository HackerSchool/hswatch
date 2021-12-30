package com.hswatch;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.hswatch.bluetooth.GPSListener;
import com.hswatch.databinding.SettingsActivityBinding;

import java.util.Objects;

//TODO(documentar)
public class SettingsActivity extends AppCompatActivity {

    SettingsActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SettingsActivityBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        binding.settingsToolbar.setTitle(R.string.MENU_DEFINICOES);
        setSupportActionBar(binding.settingsToolbar);
        try {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            EditTextPreference editTextPreference = findPreference("horas");
            if (editTextPreference != null) {
                editTextPreference.setOnBindEditTextListener(editText -> editText
                        .setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED));

            }

            SwitchPreferenceCompat switchPreferenceCompat = findPreference("gps_switch");
            if (switchPreferenceCompat != null) {
                if (switchPreferenceCompat.isChecked()) {
                    GPSListener.getInstance(getContext()).start();
                } else {
                    GPSListener.getInstance(getContext()).stop();
                }
            }

            EditTextPreference apiEditTextPreference = findPreference(getString(R.string.KEY_API_PREFERENCES));
            if (apiEditTextPreference != null) {
                apiEditTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        String apiKey = apiEditTextPreference.getText();
                        if (!apiKey.isEmpty()) {
                            Utils.testAPI(apiKey, getContext(), responseSucceed -> {
                                if (responseSucceed) {
                                    Toast.makeText(
                                            getContext(),
                                            getString(R.string.toast_API_key_accepting),
                                            Toast.LENGTH_SHORT
                                    ).show();
                                } else {
                                    Toast.makeText(
                                            getContext(),
                                            getString(R.string.toast_API_key_error),
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            });
                        }
                        return false;
                    }
                });
            }

        }
    }
}