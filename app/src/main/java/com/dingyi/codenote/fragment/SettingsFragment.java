package com.dingyi.codenote.fragment;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.dingyi.codenote.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG ="SettingsFragment" ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setSharedPreferencesName("settings");
        addPreferencesFromResource(R.xml.settings);

    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        switch (preference.getTitle().toString()){
            case "联系我":

        }

        return super.onPreferenceTreeClick(preference);
    }
}
