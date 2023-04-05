package com.example.audiobookcanvas;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragmentCompat
{

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        //read the shared preferences xml file and map it to whatever keys you have in your layout of your settings screen
        //this sets the values of the Shared Preferences object
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        // Get a reference to the Shared Preferences object
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        //we now have a reference to the Shared Preferences object
        //read the value of the skipGetStarted switch from it
        boolean switchValue = sharedPreferences.getBoolean("skipGetStarted", false);

        // Use the switch value in your code
        if (switchValue) {
            // Enable a feature
        } else {
            // Disable a feature
        }
    }

    //if you want to read the values of your settings toggles real time, and react to them, you need to make separate event handlers for each settings object
}