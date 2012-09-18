/**
 * 
 */
package org.dyndns.datsnet.myflickr;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

/**
 * @author atsumi
 * 
 */
public class SettingActivity extends PreferenceActivity {
	
	private static final String PUBLIC_FLAG_PREFERENCE = "public_flag_preference";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting);

		// CheckBoxPreference の取得
		CheckBoxPreference publicCheckBoxPreferrence = (CheckBoxPreference) findPreference(getString(R.string.public_flag_preference));
		publicCheckBoxPreferrence.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// TODO Auto-generated method stub
				SharedPreferences sp = getSharedPreferences(BaseActivity.PREFS_NAME, Context.MODE_PRIVATE);
				Editor editor = sp.edit();
//				editor.putString(PUBLIC_FLAG_PREFERENCE, newValue);
				editor.putInt(PUBLIC_FLAG_PREFERENCE, (Integer) newValue);
				editor.commit();
				return false;
			}
		});
	       
	}

}
