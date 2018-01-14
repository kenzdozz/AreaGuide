package com.ahtaya.chidozie.areaguide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    /**
     * Configure back button to restart HomeActivity to refresh content
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent settingsIntent = new Intent(this, HomeActivity.class);
        startActivity(settingsIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    /**
     * Class called from activity_settings xml file to inflate preference xml
     */
    public static class GuidePreference extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            //Creating preferences and binding to summary
            Preference sortBy = findPreference(getString(R.string.pref_sort_by_key));       //sort preference
            bindPreferenceSummaryToValue(sortBy);
            Preference resultTypes = findPreference(getString(R.string.pref_types_key));    //types preference
            bindPreferenceSummaryToValue(resultTypes);
        }

        /**
         * Handling preference changes
         */
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            String stringValue = value.toString();
            //Handling ListPreference
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] entry = listPreference.getEntries();
                    preference.setSummary(entry[prefIndex]);
                }
                return true;
            }

            //Handling MultiSelectListPreference
            if (preference instanceof MultiSelectListPreference) {
                MultiSelectListPreference multiSelectListPreference = (MultiSelectListPreference) preference;

                CharSequence[] entries = multiSelectListPreference.getEntries();    //Get Entries
                Set<String> setValues = multiSelectListPreference.getValues();      //Get set Values

                StringBuilder builder = new StringBuilder();
                for (String setValue : setValues) {
                    //Finding MultiSelectListPreference index from Value
                    int index = ((MultiSelectListPreference) preference).findIndexOfValue(setValue);
                    if (builder.length() != 0) {
                        builder.append(", ");
                    }
                    //Getting entry from index
                    builder.append(entries[index].toString());
                }
                //Setting summary
                preference.setSummary(builder.toString());
                return true;
            }
            preference.setSummary(stringValue);
            return true;
        }

        /**
         * Method to bind summary to value
         */
        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());

            if (preference instanceof MultiSelectListPreference) {
                onPreferenceChange(preference, "");
                return;
            }
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }
    }
}
