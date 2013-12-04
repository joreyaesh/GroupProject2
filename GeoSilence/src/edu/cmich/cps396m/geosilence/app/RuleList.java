package edu.cmich.cps396m.geosilence.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.fortysevendeg.swipelistview.sample.adapters.PackageAdapter;
import com.fortysevendeg.swipelistview.sample.adapters.PackageItem;

import java.util.ArrayList;
import java.util.List;

import edu.cmich.cps396m.geosilence.R;
import edu.cmich.cps396m.geosilence.Rule;
import edu.cmich.cps396m.geosilence.StorageManager;
import edu.cmich.cps396m.geosilence.StorageManager.DataException;
import edu.cmich.cps396m.geosilence.service.PollReceiver;

/**
 * Main activity displaying a list of rules
 */
public class RuleList extends Activity {

	private static final int AR = Integer.MAX_VALUE; // request code for "Add Rule"
	
	private StorageManager storageManager;

	// Custom ListView stuff
	private PackageAdapter adapter;
	private List<PackageItem> data;
	private SwipeListView swipeListView;
	private ProgressDialog progressDialog;

	// Message types sent from the PackageAdapter for the Handler
	public static final int MESSAGE_EDIT_RULE = 1;
	public static final int MESSAGE_DISABLE_RULE = 2;
	public static final int MESSAGE_DELETE_RULE = 3;
    public static final int MESSAGE_MOVE_RULE_UP = 4;
    public static final int MESSAGE_MOVE_RULE_DOWN = 5;

    private Context mContext = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rule_list);

		// start up the service
		PollReceiver.scheduleAlarms(mContext);

		// handles all rule storage
		storageManager = new StorageManager(mContext);

		// set up the custom ListView
		setupSwipeListView();
	}

	private void setupSwipeListView() {
		data = new ArrayList<PackageItem>();
		adapter = new PackageAdapter(mContext, data, mHandler);
		swipeListView = (SwipeListView) findViewById(R.id.example_lv_list);

		swipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
			@Override
            public void onOpened(int position, boolean toRight) {
                // Make sure the Enable/Disable button is updated
                updateButtonText(position);
			}

			@Override
			public void onClosed(int position, boolean fromRight) {
			}

			@Override
			public void onListChanged() {
			}

			@Override
			public void onMove(int position, float x) {
			}

			@Override
			public void onStartOpen(int position, int action, boolean right) {
				Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
                // Make sure the Enable/Disable button is updated
                updateButtonText(position);
			}

			@Override
			public void onStartClose(int position, boolean right) {
				Log.d("swipe", String.format("onStartClose %d", position));
			}

			@Override
			public void onClickFrontView(int position) {
				Log.d("swipe", String.format("onClickFrontView %d", position));
			}

			@Override
			public void onClickBackView(int position) {
				Log.d("swipe", String.format("onClickBackView %d", position));
			}

			@Override
			public void onDismiss(int[] reverseSortedPositions) {
				for (int position : reverseSortedPositions) {
					data.remove(position);
				}
				adapter.notifyDataSetChanged();
			}

		});

		swipeListView.setAdapter(adapter);

		reload();

		new ListAppTask().execute();

		progressDialog = new ProgressDialog(mContext);
		progressDialog.setMessage("Loading");
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	private void reload() {
		swipeListView.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
		swipeListView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);
		swipeListView.setSwipeActionRight(SwipeListView.SWIPE_ACTION_REVEAL);
		swipeListView.setOffsetLeft(convertDpToPixel(0));
		swipeListView.setOffsetRight(convertDpToPixel(0));
		swipeListView.setAnimationTime(0);
		swipeListView.setSwipeOpenOnLongPress(true);
	}

	public int convertDpToPixel(float dp) {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return (int) px;
	}

	// The Handler that gets information back from the PackageAdapter
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// get index and a rule selected
			int position = msg.arg1;
			Rule rule = storageManager.getRules().get(position);
			switch (msg.what) {
			// EDIT button
			case MESSAGE_EDIT_RULE:
				Intent i = new Intent(getApplicationContext(), AddEditRule.class);
				i.putExtra(AddEditRule.NRL, rule);
				startActivityForResult(i, position);
				break;
			// ENABLE/DISABLE button
			case MESSAGE_DISABLE_RULE:
                boolean status = rule.isActive();
				rule.setActive(!status);
                storageManager.write();
				Toast.makeText(mContext,
						"Rule " + rule.getName() + (status ? " disabled" : " enabled"), Toast.LENGTH_SHORT)
						.show();
                // update the SwipeListView
                updateButtonText(position);
                new ListAppTask().execute();
				break;
			// DELETE button
			case MESSAGE_DELETE_RULE:
				storageManager.deleteRule(position);
                new ListAppTask().execute();
				swipeListView.closeAnimate(position);
				Toast.makeText(mContext, "Rule " + rule.getName() + " deleted", Toast.LENGTH_SHORT)
						.show();
				break;
            // Up arrow
            case MESSAGE_MOVE_RULE_UP:
                storageManager.moveUp(position);
                Log.d("GS", "Rule moved up");
                new ListAppTask().execute();
                break;
            // Down arrow
            case MESSAGE_MOVE_RULE_DOWN:
                storageManager.moveDown(position);
                Log.d("GS", "Rule moved down");
                new ListAppTask().execute();
                break;
			}
		}
	};

    private void updateButtonText(int position) {
        Rule rule = storageManager.getRules().get(position);
        Button button = (Button) swipeListView.getChildAt(position)
                .findViewById(R.id.back)
                .findViewById(R.id.button_disable_rule);
        button.setText((rule.isActive() ? "Disable" : "Enable"));
        new ListAppTask().execute();
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rule_list, menu);
		return true;
	}

	/**
	 * Handling result from AddEditRule activity
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("GS", "Result received with resultCode="+resultCode+",requestCode:"+requestCode);
		if (resultCode == RESULT_OK) {
			if (requestCode == AR) {
				// if a new rule was returned from AddEditRule, save it and refresh list
				Bundle b = data.getExtras();
				Rule newRule = (Rule) b.get(AddEditRule.NRL);
				try {
					storageManager.addRule(newRule);
                    new ListAppTask().execute();
					Toast.makeText(mContext, "New rule saved", Toast.LENGTH_LONG).show();
					Log.d("GS", "New rule added: " + newRule.toString());
				} catch (DataException e) {
					Toast.makeText(mContext, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
					Log.e("GS", "Error saving new rule: " + e.getMessage());
				}
			} else {
				// replacing the rule in StorageManager
				Bundle bb = data.getExtras();
				Rule updatedRule = (Rule) bb.get(AddEditRule.NRL);
				try {
					storageManager.replaceRule(requestCode, updatedRule);
				} catch (DataException e) {
					Toast.makeText(mContext, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
					Log.e("GS", "Error saving updated rule: " + e.getMessage());
				}

                new ListAppTask().execute();
				Toast.makeText(mContext, "Rule " + updatedRule.getName() + " updated", Toast.LENGTH_LONG).show();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			Intent i = new Intent(mContext, AddEditRule.class);
			startActivityForResult(i, AR);
			break;
		case R.id.action_settings:
			Intent pref_intent = new Intent(mContext, Preferences.class);
			startActivity(pref_intent);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	public class ListAppTask extends AsyncTask<Void, Void, List<PackageItem>> {

		protected List<PackageItem> doInBackground(Void... args) {
			List<Rule> rules = storageManager.getRules();

			List<PackageItem> data = new ArrayList<PackageItem>();

			for (Rule rule : rules) {
				try {
					PackageItem item = new PackageItem(rule);
					data.add(item);
				} catch (Exception e) {
					// Do nothing
				}
			}

			return data;
		}

		protected void onPostExecute(List<PackageItem> result) {
			data.clear();
			data.addAll(result);

            // Update the profile when the UI updates
            PollReceiver.scheduleAlarms(mContext);

			adapter.notifyDataSetChanged();
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	}
}
