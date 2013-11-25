package edu.cmich.cps396m.geosilence;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Vibrator;
import android.widget.Toast;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class SuperBackroundAgent extends WakefulIntentService{
public static final int PROFILE_SILENT = 1;
public static final int PROFILE_VIBRATE = 2;
public static final int PROFILE_NORMAL = 3;
public static final int PROFILE_LOUD = 4;


	public SuperBackroundAgent(String name) {        //possibly define vars here
		super(name);	
	}
	
	@Override
	public void onCreate() {                           //location update
		StorageManager storageManager = new StorageManager(this);
		List<Rule> rules = storageManager.getRules();
		
		for(int i = 0; i<rules.size(); i++){
			double Lat = rules.get(i).getLat();
			double Long = rules.get(i).getLan();
			double Radius = rules.get(i).getRadius();
			checkLocation(Lat,Long,Radius);
		}
		
		
		
		
		//get location here
		
		
	 }
	
	private void checkLocation(double lat, double l, double radius) {
          GPSTracker gps=new GPSTracker(this);  //init GPS
    	
    	double Lat=0;
    	double Long=0;  // Vars to 
    	
    	 if(gps.canGetLocation()){
              Lat = gps.getLatitude();
              Long = gps.getLongitude();
             Toast.makeText(getApplicationContext(), "We Have You Now.\nLat: " + Lat + "\nLong: " + Long, Toast.LENGTH_LONG).show();    
         }else{
     		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
     		Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER); 
        	Lat = loc.getLatitude();
        	Long = loc.getLongitude();
         }
    	 
    	 
		
	}

	@Override
	  protected void doWakefulWork(Intent intent) {    //Scheualed and loaded, ready to do some work
		//TODO remove next lines
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(500);
		
		this.onCreate(); // get location
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
