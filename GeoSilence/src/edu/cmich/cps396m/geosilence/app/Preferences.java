package edu.cmich.cps396m.geosilence.app;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import edu.cmich.cps396m.geosilence.R;

public class Preferences extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.user_preference);
		
		this.getFragmentManager().beginTransaction()
		.replace(R.id.prefInsert, new MyPreferenceFragment())
		.commit();
		
	}
	
	public static class MyPreferenceFragment extends PreferenceFragment{
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
		}
	}
	
	
}
