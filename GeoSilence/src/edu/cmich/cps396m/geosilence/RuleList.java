package edu.cmich.cps396m.geosilence;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.fortysevendeg.swipelistview.sample.adapters.PackageAdapter;
import com.fortysevendeg.swipelistview.sample.adapters.PackageItem;

import edu.cmich.cps396m.geosilence.StorageManager.DataException;

/**
 * Main activity displaying a list of rules
 */
public class RuleList extends Activity {

	private static final int AR = 5786431;	//request code for "Add Rule"
    private static final int ER = 5786432; // request code for "Edit Rule"
	
	private StorageManager storageManager;
	private AudioManager audioManager;

    // Custom ListView stuff
    private PackageAdapter adapter;
    private List<PackageItem> data;
    private SwipeListView swipeListView;
    private ProgressDialog progressDialog;

    // Message types sent from the PackageAdapter for the Handler
    public static final int MESSAGE_EDIT_RULE = 1;
    public static final int MESSAGE_DISABLE_RULE = 2;
    public static final int MESSAGE_DELETE_RULE = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rule_list);
		PollReceiver.scheduleAlarms(this); //Make service persistent (no real way to make an UNKILLABLE app)
		storageManager = new StorageManager(this);	//handles all rule storage
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);  //handles sound profile changing
		
		//set up the custom ListView
        setupSwipeListView();

	}

    private void setupSwipeListView() {
        data = new ArrayList<PackageItem>();
        adapter = new PackageAdapter(this, data, mHandler);
        swipeListView = (SwipeListView) findViewById(R.id.example_lv_list);

        swipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
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

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void reload() {
        swipeListView.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH);
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
            int position = msg.arg1;
            Rule rule = storageManager.getRules().get(position);
            switch (msg.what) {
                case MESSAGE_EDIT_RULE:
                    Intent i = new Intent(getApplicationContext(), AddEditRule.class);
                    i.putExtra(AddEditRule.NRL, rule);
                    startActivityForResult(i, ER);
                    break;
                case MESSAGE_DISABLE_RULE:
                    if(rule.isActive()){
                        rule.setActive(false);
                        data.get(position).setActive(false);
                        Toast.makeText(getApplicationContext(), "Rule " + rule.getName() + " disabled", Toast.LENGTH_SHORT).show();
                        // update the SwipeListView
                        adapter.notifyDataSetChanged();
                        storageManager.write();
                        break;
                    }
                    rule.setActive(true);
                    data.get(position).setActive(true);
                    Toast.makeText(getApplicationContext(), "Rule " + rule.getName() + "  enabled", Toast.LENGTH_SHORT).show();
                    // update the SwipeListView
                    adapter.notifyDataSetChanged();
                    storageManager.write();
                    break;
                case MESSAGE_DELETE_RULE:
                    storageManager.deleteRule(position);
                    data.remove(data.get(position));
                    adapter.notifyDataSetChanged();
                    swipeListView.closeAnimate(position);
                    Toast.makeText(getApplicationContext(), "Rule " + rule.getName() + " deleted", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rule_list, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case AR:
			if (resultCode == RESULT_OK) {
				//if a new rule was returned from AddEditRule, save it and refresh list
				Bundle b = data.getExtras();
				Rule newRule = (Rule) b.get(AddEditRule.NRL);
				try {
					storageManager.addRule(newRule);
                    PackageItem item = new PackageItem(newRule);
                    this.data.add(item);
					Toast.makeText(this, "New rule saved", Toast.LENGTH_LONG).show();
                    // update the SwipeListView
					adapter.notifyDataSetChanged();
				} catch (DataException e) {
					Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
			break;
            case ER:
                if (resultCode == RESULT_OK) {
                    //if an edited rule was returned from AddEditRule, save it and refresh list
                    Bundle b = data.getExtras();
                    Rule updatedRule = (Rule) b.get(AddEditRule.NRL);
                    storageManager.replaceRule(updatedRule);
                    PackageItem item = new PackageItem(updatedRule);
                    PackageItem oldItem = new PackageItem(storageManager.findRuleByName(updatedRule.getName()));
                    // get index of old item
                    int index = -1;
                    for(PackageItem packageItem : this.data) {
                        if (packageItem.getName().equals(item.getName()))
                            index = this.data.indexOf(packageItem);
                    }
                    // remove the old item
                    this.data.remove(index);
                    // add the new item to the same location that the old one was removed from
                    this.data.add(index, item);
                    Toast.makeText(this, "Rule " + updatedRule.getName() + " updated", Toast.LENGTH_LONG).show();
                    // update the SwipeListView
                    swipeListView.closeAnimate(index);
                    adapter.notifyDataSetChanged();
                }
                break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			Intent i = new Intent(this, AddEditRule.class);
			startActivityForResult(i, AR);
			break;
		case R.id.action_settings:
			Intent pref_intent = new Intent(this, Preferences.class);
			startActivity(pref_intent);
			break;
		
		//temporary - manual sound profiles
		case R.id.silent_mode:
			audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			break;
		case R.id.normal_mode:
			audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			break;
		case R.id.vibrate_mode:
			audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
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
            adapter.notifyDataSetChanged();
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }
}
