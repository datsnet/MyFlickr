/**
 *
 */
package org.dyndns.datsnet.myflickr;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

/**
 * @author atsumi
 *
 */
public class SettingActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting);

		// CheckBoxPreference の取得
		ListPreference listPreferrence = (ListPreference) findPreference(getString(R.string.release_setting_key));
		SharedPreferences sp = getSharedPreferences(BaseActivity.PREFS_NAME, Context.MODE_PRIVATE);
		setReleaseValueView(listPreferrence, sp.getInt(BaseActivity.Release_FLAG_PREFERENCE, -1));
		listPreferrence.setOnPreferenceChangeListener(onPreferenceChangeListener);

	}

	private boolean setReleaseValueView(Preference preference, int value) {

		switch (value) {
		case 0:
			preference.setSummary(R.string.release_setting_preference_public);
			break;
		case 1:
			preference.setSummary(R.string.release_setting_preference_not_public);
			break;
		case 2:
			preference.setSummary(R.string.release_setting_preference_family);
			break;
		case 3:
			preference.setSummary(R.string.release_setting_preference_friend);
			break;
		case 4:
			preference.setSummary(R.string.release_setting_preference_family_friend);
			break;

		default:
			preference.setSummary("指定なし：公開になります");
			break;
		}
		return true;

	}

	private OnPreferenceChangeListener onPreferenceChangeListener = new OnPreferenceChangeListener() {

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			// TODO Auto-generated method stub
			SharedPreferences sp = getSharedPreferences(BaseActivity.PREFS_NAME, Context.MODE_PRIVATE);
			// editor.putString(PUBLIC_FLAG_PREFERENCE, newValue);
			Editor editor = sp.edit();
			// return false;

			if (preference.getKey().equals(getString(R.string.release_setting_key))) {
				int value = Integer.parseInt((String) newValue);

				setReleaseValueView(preference, value);

				editor.putInt(BaseActivity.Release_FLAG_PREFERENCE, value);
				editor.commit();

				return true;
			}

			return false;

		}
	};

}
