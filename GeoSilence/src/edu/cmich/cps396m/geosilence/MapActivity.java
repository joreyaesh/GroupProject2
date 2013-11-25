package edu.cmich.cps396m.geosilence;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Activity to select a location on map and radius for it.
 * When calling with intent you can pass it parameters:
 *  - MapActivity.LOCATION: a LatLng object
 *  - MapActivity.RADIUS: a double value in feet(!)
 * if you want some location to be shown when activity is opened.
 * Location picked is returned through extras with the same parameter keys
 */
public class MapActivity extends Activity {
	//possible values of radius in feet
	public static final List<Integer> radiusValue = Arrays.asList(10, 30, 50, 100, 250, 500, 1000, 2000, 5000, 10000, 50000, 100000, 500000);
	private static final int DEFAULT_RADIUS = 0;
	
	//intent data for location and radius
	public static final String LOCATION = "LocLatLng";
	public static final String RADIUS = "RadiusValueAndIWantThisToBeALongStringJustBecause";
	public static final double FEET_IN_METER = 3.28084;
	
	private GoogleMap gmap;
	private SeekBar sb;
	private Marker marker;
	private Circle circle;
	private int radius;	//in feet

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		//getting a handle on GoogleMap and setting onclick listener
		gmap = ((MapFragment) getFragmentManager().findFragmentById(R.id.selectLocationMap)).getMap();
		gmap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng point) {
				updateMarker(point);
			}
		});
		
		//setting max progress value and onProgrweessChanged listener for radius seekbar
		sb = (SeekBar) findViewById(R.id.seekBarRadius);
		sb.setMax(radiusValue.size() - 1);
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				radius = radiusValue.get(progress);
				updateRadius();
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});
		
		//checking if existing location and radius are passed to activity
		Bundle b = getIntent().getExtras();
		LatLng loc = (LatLng)b.get(LOCATION);
		if (loc != null) {
			updateMarker(loc);
		}
		//if radius not passed, setting to default and updating
		if (0 == (radius = b.getInt(RADIUS))) {
			radius = radiusValue.get(DEFAULT_RADIUS);
		}
		updateRadius();
	}
	
	/**
	 * updating the radius in text and on map
	 */
	private void updateRadius() {
		//updating text
		((TextView) findViewById(R.id.textViewSelectRadius)).setText(getString(R.string.textSelectRadius) + ": " + radius + "ft");
		//draw circle
		if (circle != null)
			circle.remove();
		if (marker != null) {
			CircleOptions co = new CircleOptions()
			.center(marker.getPosition())
			.radius(radius / FEET_IN_METER)
			.fillColor(Color.argb(64, 0, 0, 256))
			.strokeColor(Color.BLUE);
			circle = gmap.addCircle(co);
		}
	}
	
	/**
	 * sets marker at given location, replacing old one if exists
	 * @param point
	 */
	private void updateMarker(LatLng point) {
		if (marker != null)
			marker.remove();
		marker = gmap.addMarker(new MarkerOptions().position(point));
		updateRadius();
	}
	
	/**
	 * returning location picked
	 */
	private void saveLocationAndReturn() {
		Intent data = new Intent();
		data.putExtra(LOCATION, marker.getPosition());
		data.putExtra(RADIUS, radius);
		setResult(RESULT_OK, data);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_select:
			//verifying a location has been selected
			if (marker == null) {
				Toast.makeText(this, "Please tap on map to select location", Toast.LENGTH_SHORT).show();
				return false;
			}
			saveLocationAndReturn();
			break;
		case R.id.action_satellite:
			//TODO
			break;
		case R.id.action_normal:
			//TODO
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	/**
	 * when back button is pressed, if location has been picked, it will be saved and returned
	 */
	@Override
	public void onBackPressed() {
		if (marker != null) {
			saveLocationAndReturn();
		}
		super.onBackPressed();
	}
	
}
