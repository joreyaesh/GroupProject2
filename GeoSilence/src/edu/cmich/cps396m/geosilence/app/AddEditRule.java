package edu.cmich.cps396m.geosilence.app;

import java.util.prefs.Preferences;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import edu.cmich.cps396m.geosilence.R;
import edu.cmich.cps396m.geosilence.Rule;
import edu.cmich.cps396m.geosilence.StorageManager;
import edu.cmich.cps396m.geosilence.R.id;
import edu.cmich.cps396m.geosilence.R.layout;
import edu.cmich.cps396m.geosilence.R.menu;
import edu.cmich.cps396m.geosilence.R.string;

/**
 * Activity to add a new rule
 */
public class AddEditRule extends Activity {
	// intent data key for new rule created but this activity
	public static final String NRL = "NEW_RULE";

	private static final int GET_LOC = 8645733; // request code for get location
												// call

	private LatLng latLng;
	private double radius;
	private char[] weekdays;

	private Rule existing_rule;

	/**
	 * handler for Pick Location button. starts MapActivity
	 */
	public void setLocationHandler(View view) {
		Intent i = new Intent(this, MapActivity.class);
		i.putExtra(MapActivity.LOCATION, latLng);
		i.putExtra(MapActivity.RADIUS, radius);
		startActivityForResult(i, GET_LOC);
	}

	/**
	 * sets current location values
	 * 
	 * @param latLng
	 * @param radius
	 */
	private void setLocationData(LatLng latLng, double radius) {
		this.latLng = latLng;
		this.radius = radius;
		if (this.latLng != null) {
			Button button = (Button) findViewById(R.id.buttonSetLocation);
			button.setText(getString(R.string.textButtonChangeLocation));
		}
	}

	/**
	 * result from MapActivity
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case GET_LOC:
			if (resultCode == RESULT_OK) {
				Bundle b = data.getExtras();
				setLocationData((LatLng) b.get(MapActivity.LOCATION),
						b.getDouble(MapActivity.RADIUS));
			}
			break;
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
		
		if (getIntent().hasExtra(NRL)) {
			existing_rule = (Rule) getIntent().getExtras().get(NRL);
			setLocationData(new LatLng(existing_rule.getLat(), existing_rule.getLan()), existing_rule.getRadius()) ;
			
			// TODO
			existing_rule.getStartTime();
			// TODO
			existing_rule.getEndTime();
			// TODO
			existing_rule.getMode();
						
			((EditText) findViewById(R.id.editTextName)).setText(existing_rule.getName());
		}
		else {
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_edit_rule, menu);
		return true;
	}

	// generates a Rule object out of information enetered in this activity.
	// Returns null if any errors are present
	private Rule generateRule() {
		StorageManager sm = new StorageManager(this);
		// verify name
		String name = ((EditText) findViewById(R.id.editTextName)).getText()
				.toString();
		if (name.isEmpty()) {
			Toast.makeText(this, "Please enter the name", Toast.LENGTH_SHORT)
					.show();
			return null;
		}
		if (existing_rule == null && sm.findRuleByName(name) != null) {
			Toast.makeText(this, "A rule with this name already exists",
					Toast.LENGTH_SHORT).show();
			return null;
		}
		// verify that marker is set
		if (latLng == null) {
			Toast.makeText(this, "Please select location", Toast.LENGTH_SHORT)
					.show();
			return null;
		}
		// TODO mode
		// TODO time and days
		boolean[] wd = {true, true, true, true, true, true, true};
		return new Rule(name, latLng.latitude, latLng.longitude, radius, 0,
				wd, 0, 0, true);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_save:
			// generate and validate new rule
			Rule newRule = generateRule();
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
