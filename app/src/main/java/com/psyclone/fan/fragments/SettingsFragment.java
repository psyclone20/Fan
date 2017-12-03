package com.psyclone.fan.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.psyclone.fan.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
