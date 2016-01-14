package jjwork.jjtest02;

import jjwork.jj_test02.R;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

public class SettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
    }
    public class PrefsFragment extends PreferenceFragment {
    	@Override
    	public void onCreate(Bundle savedInstanceState) {
    		// TODO Auto-generated method stub
    		super.onCreate(savedInstanceState);
    		addPreferencesFromResource(R.xml.preferences);
    	}
    	
    	@Override
    	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
    			Preference preference) {
    		// TODO Auto-generated method stub
    		if("recoder_datas_chk".equals(preference.getKey())) {
    			CheckBoxPreference chkPreference = (CheckBoxPreference)findPreference("recoder_datas_chk");
    			ListPreference listPreference = (ListPreference)findPreference("recoder_max_list");
    			
    			listPreference.setEnabled(chkPreference.isChecked());
    		}
    		
    		
    		return super.onPreferenceTreeClick(preferenceScreen, preference);
    	}
    }
}
