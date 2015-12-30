package jjwork.jj_test02;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class SettingActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
