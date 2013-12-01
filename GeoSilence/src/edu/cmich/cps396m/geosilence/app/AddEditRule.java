package edu.cmich.cps396m.geosilence.app;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.LatLng;

import edu.cmich.cps396m.geosilence.R;
import edu.cmich.cps396m.geosilence.Rule;
import edu.cmich.cps396m.geosilence.StorageManager;

/**
 * Activity to add a new rule
 */
public class AddEditRule extends Activity {
	// intent data key for new rule created but this activity
	public static final String NRL = "NEW_RULE";

	// request code for MapActivity call
	private static final int GET_LOC = 8645733;

	private Rule workingRule;
	private String originalName;
	
	/**
	 * sets current location values
	 * 
	 * @param latLng
	 * @param radius
	 */
	private void setLocationData(LatLng latLng, int radius) {
		workingRule.setLat(latLng.latitude);
		workingRule.setLng(latLng.longitude);
		workingRule.setRadius(radius);
		if (latLng != null) {
			Button button = (Button) findViewById(R.id.buttonSetLocation);
			button.setText(getString(R.string.textButtonChangeLocation));
		}
	}

	/**
	 * handler for Pick Location button. starts MapActivity
	 */
	public void setLocationHandler(View view) {
		Intent i = new Intent(this, MapActivity.class);
		LatLng latLng = new LatLng(workingRule.getLat(), workingRule.getLng());
		i.putExtra(MapActivity.LOCATION, latLng);
		i.putExtra(MapActivity.RADIUS, workingRule.getRadius());
		startActivityForResult(i, GET_LOC);
	}

	/**
	 * handling result from MapActivity - setting location data
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == GET_LOC) {
			Bundle b = data.getExtras();
			setLocationData((LatLng) b.get(MapActivity.LOCATION), b.getInt(MapActivity.RADIUS));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * initializes rule values if an existing rule is passed
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_edit_rule);

		//if an existing rule is passed using that rule
		//otherwise generating a new default rule
		if (getIntent().hasExtra(NRL)) {
			workingRule = (Rule) getIntent().getExtras().get(NRL);
//            // Don't allow the name to be changed, as it is used when
//            // replacing the old rule with the new one
//            EditText nameField = (EditText) findViewById(R.id.editTextName);
//            nameField.setFocusableInTouchMode(false);
//            nameField.setFocusable(false);
//            nameField.setClickable(false);
		} else {
			workingRule = new Rule();
		}
		originalName = workingRule.getName();
			
		setLocationData(new LatLng(workingRule.getLat(), workingRule.getLng()), workingRule.getRadius());

		// TODO start/end time data
		
		//setting weekday buttons
		boolean[] wd = workingRule.getWeekdays();
		((ToggleButton) findViewById(R.id.toggleButtonSun)).setChecked(wd[0]);
		((ToggleButton) findViewById(R.id.toggleButtonMon)).setChecked(wd[1]);
		((ToggleButton) findViewById(R.id.toggleButtonTue)).setChecked(wd[2]);
		((ToggleButton) findViewById(R.id.toggleButtonWed)).setChecked(wd[3]);
		((ToggleButton) findViewById(R.id.toggleButtonThu)).setChecked(wd[4]);
		((ToggleButton) findViewById(R.id.toggleButtonFri)).setChecked(wd[5]);
		((ToggleButton) findViewById(R.id.toggleButtonSat)).setChecked(wd[6]);
		
		// setting mode radio button
		switch (workingRule.getMode()) {
		case AudioManager.RINGER_MODE_NORMAL:
			((RadioButton)findViewById(R.id.radioButtonNormalMode)).setChecked(true);
			break;
		case AudioManager.RINGER_MODE_SILENT:
			((RadioButton)findViewById(R.id.radioButtonSilentMode)).setChecked(true);
			break;
		case AudioManager.RINGER_MODE_VIBRATE:
			((RadioButton)findViewById(R.id.radioButtonVibrateMode)).setChecked(true);
			break;
		}
		
		((EditText) findViewById(R.id.editTextName)).setText(workingRule.getName());
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_edit_rule, menu);
		return true;
	}

	/**
	 * validates necessary fields and returns a valid Rule Object or displays an error Toast and returns null 
	 * @return
	 * valid Rule object created by this activity or null if errors are present
	 */
	private Rule getValidRule() {
		//verifying that name entered is not empty
		String name = ((EditText) findViewById(R.id.editTextName)).getText().toString();
		if (name.isEmpty()) {
			Toast.makeText(this, "Please enter the name", Toast.LENGTH_SHORT).show();
			return null;
		}		
		//if name was changed (in editing rule) validate that name is unique
		StorageManager sm = new StorageManager(this);
		if (!name.equals(originalName) && sm.findRuleByName(name) != null) {
			Toast.makeText(this, "A rule with this name already exists", Toast.LENGTH_SHORT).show();
			return null;
		}		
		// verify that location and radius are set
		if (workingRule.getLat() == 0 && workingRule.getLng() == 0 && workingRule.getRadius() == 0) {
			Toast.makeText(this, "Please select location", Toast.LENGTH_SHORT).show();
			return null;
		}
		// find mode selected and verify it
		int mode;
		if (((RadioButton)findViewById(R.id.radioButtonSilentMode)).isChecked())
			mode = AudioManager.RINGER_MODE_SILENT;
		else if (((RadioButton)findViewById(R.id.radioButtonVibrateMode)).isChecked())
			mode = AudioManager.RINGER_MODE_VIBRATE;
		else if (((RadioButton)findViewById(R.id.radioButtonNormalMode)).isChecked())
			mode = AudioManager.RINGER_MODE_NORMAL;
		else if (((RadioButton)findViewById(R.id.radioButtonLoudMode)).isChecked())
			mode = 500;
		else {
			Toast.makeText(this, "Please select ringer mode", Toast.LENGTH_SHORT).show();
			return null;
		}
		// TODO time and days
		int startTime =0;
		int endTime = 0;
		//weekdays
		boolean[] wd = new boolean[7];
		wd[0] = ((ToggleButton)findViewById(R.id.toggleButtonSun)).isChecked();
		wd[1] = ((ToggleButton)findViewById(R.id.toggleButtonMon)).isChecked();
		wd[2] = ((ToggleButton)findViewById(R.id.toggleButtonTue)).isChecked();
		wd[3] = ((ToggleButton)findViewById(R.id.toggleButtonWed)).isChecked();
		wd[4] = ((ToggleButton)findViewById(R.id.toggleButtonThu)).isChecked();
		wd[5] = ((ToggleButton)findViewById(R.id.toggleButtonFri)).isChecked();
		wd[6] = ((ToggleButton)findViewById(R.id.toggleButtonSat)).isChecked();
		//validating that at least one weekday is checked
		if (!(wd[0] || wd[1] || wd[2] || wd[3] || wd[4] || wd[5] || wd[6])) {
			Toast.makeText(this, "Please select at least one weekday", Toast.LENGTH_SHORT).show();
			return null;
		}
		
		return new Rule(name, workingRule.getLat(), workingRule.getLng(), workingRule.getRadius(), mode, wd, startTime, endTime, true);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_save:
			Rule newRule = getValidRule();
			if (newRule != null) {
				// return data
				Intent data = new Intent();
				data.putExtra(NRL, newRule);
				setResult(RESULT_OK, data);
				finish();
			}
			break;
		case R.id.action_settings:
			Intent pref_intent = new Intent(this, Preferences.class);
			startActivity(pref_intent);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

}
