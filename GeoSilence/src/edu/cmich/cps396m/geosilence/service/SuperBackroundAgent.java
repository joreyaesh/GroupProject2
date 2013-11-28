package edu.cmich.cps396m.geosilence.service;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.util.Log;
import android.widget.Toast;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import edu.cmich.cps396m.geosilence.Rule;
import edu.cmich.cps396m.geosilence.StorageManager;

public class SuperBackroundAgent extends WakefulIntentService{

	int weekday;
	int time;
	Location loc;
	StorageManager storageManager; 
	GPSTracker gps;
	
	public SuperBackroundAgent() {        //possibly define vars here
		super("ScheduledService");
	}
	
	private void checkLocationAndTime() {
          gps=new GPSTracker(this);  //init GPS
    	    	
    	 if(gps.canGetLocation() == true){
              loc = gps.getLocation();
         }else{
     		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
     		loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
         }
    	 
    	 //keep for debugging
    	 if (loc != null){
    		 Toast.makeText(getApplicationContext(), "Current Location Found", Toast.LENGTH_SHORT).show();
    	 }
    	 else{
    		 Toast.makeText(getApplicationContext(), "LOCATION NOT FOUND!!!", Toast.LENGTH_SHORT).show();
    	 }
	}

	@Override
	  protected void doWakefulWork(Intent intent) {    //Scheualed and loaded, ready to do some work
		storageManager = new StorageManager(this);
		List<Rule> rules = storageManager.getRules();
		checkLocationAndTime();
		
		Rule ruleToUse = null;
		
		for(Rule rule: rules){
			if (ruleIsTrue(rule)) {
				ruleToUse = rule;
				Toast.makeText(getApplicationContext(), "Active Rule Found", Toast.LENGTH_SHORT).show();
				break;
			}
			activateRule(ruleToUse);
		}
		
		gps.stopUsingGPS();
	  }
	
	
	private boolean ruleIsTrue(Rule rule) {
    	if (rule.isActive() && 
    			//TODO check time interval
    							//TODO check weekday
    			isWithinRadius(rule.getLat(), rule.getLng(), loc.getLatitude(), loc.getLongitude(), rule.getRadius())){
    		return true;
    	}
    	
    	else  {return false;}
		
	}
	
	private boolean isWithinRadius(double lat1, double lng1, double lat2, double lng2, double radius) {
		double distance = Math.sqrt(Math.pow(lat1 - lat2, 2) + Math.pow(lng1 - lng2, 2));		
		Log.d("GS", "Distance found: " + distance + "Radius: " + radius);
		return distance < radius;
	}
	
	private void activateRule(Rule rule) {
		//TODO change to default from settings
		int mode = AudioManager.RINGER_MODE_NORMAL;
		if (rule != null)
			mode = rule.getMode();
		
		//activate profile
		Toast.makeText(getApplicationContext(), "Changing sound profile", Toast.LENGTH_SHORT).show();
		AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
		am.setRingerMode(mode);
	}

	@Override
	public void onDestroy() {
//		AlarmManager am=(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
//        Intent i = new Intent(this, SuperBackroundAgent.class);
//        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
//        am.set(AlarmManager.RTC_WAKEUP, 1000 * 60 * 10, pi); 
	 }

//	 @Override
//	 protected void onHandleIntent(Intent workIntent) {
//	        
//	        String dataString = workIntent.getDataString();   //outdated code
//	      
//	        
//	    }


}
