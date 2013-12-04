package edu.cmich.cps396m.geosilence.service;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import java.util.Calendar;
import java.util.List;

import edu.cmich.cps396m.geosilence.Rule;
import edu.cmich.cps396m.geosilence.StorageManager;

public class SuperBackroundAgent extends WakefulIntentService{

    int weekday;
    int time;
    Location loc;
    StorageManager storageManager;
    GPSTracker gps;
    Handler mHandler;

    public SuperBackroundAgent() {        //possibly define vars here
        super(SuperBackroundAgent.class.getName());
        //super("ScheduledService");
        mHandler = new Handler();
    }

    private void checkLocation() {
        gps=new GPSTracker(this);  //init GPS

        if(gps.canGetLocation() == true){
            loc = gps.getLocation();
        }else{
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        //keep for debugging
        if (loc != null){
            //toast("Current Location Found");
        }
        else{
            //toast("LOCATION NOT FOUND!!!");
        }
    }

    private void checkTime() {
        Calendar rightNow = Calendar.getInstance();
        int day = rightNow.get(Calendar.DAY_OF_WEEK);

        weekday = day - 1;

    }

    @Override
    protected void doWakefulWork(Intent intent) {    //Scheualed and loaded, ready to do some work
        storageManager = new StorageManager(this);
        List<Rule> rules = storageManager.getRules();
        checkLocation();
        checkTime();

        Rule ruleToUse = null;

        for(int i = 0; i < rules.size() && ruleToUse == null; ++i) {
            if (ruleIsTrue(rules.get(i))) {
                ruleToUse = rules.get(i);
                //toast("Active Rule Found");
            }
        }
        activateRule(ruleToUse);
    }
    //can be deleted, only to save battery. We can make this a setting.



    private boolean ruleIsTrue(Rule rule) {
        if (rule.isActive()==true &&
                //TODO check time interval
                rule.getWeekdays()[weekday] == true &&
                isWithinRadius(rule.getLat(), rule.getLng(), loc.getLatitude(), loc.getLongitude(), rule.getRadius()) == true){
            return true;
        }

        else  {return false;}

    }

    private boolean isWithinRadius(double lat1, double lng1, double lat2, double lng2, double radius) {
        float[] results = new float[3];
        Location.distanceBetween(lat1, lng1, lat2, lng2, results);
        double distanceFt = results[0] * 3.28084;
        //Log.d("GS", "Distance found: " + distanceFt + "ft, radius: " + radius + "ft.");
        return distanceFt < radius;
    }

    private void activateRule(Rule rule) {
        //TODO change to default from settings
        int mode = AudioManager.RINGER_MODE_NORMAL;

        if (rule != null){
            mode = rule.getMode();
        }
        //activate profile

        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);

        if(am.getRingerMode() != mode){
            //toast("Changing sound profile"); // Disabled because this is displayed when a current profile (that isn't RINGER_MODE_NORMAL) is already active, and is still active after an update
            am.setRingerMode(mode);
        }



    }

    private void toast(final String text){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //@Override
    //public void onDestroy() {
//		AlarmManager am=(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
//        Intent i = new Intent(this, SuperBackroundAgent.class);
//        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
//        am.set(AlarmManager.RTC_WAKEUP, 1000 * 60 * 10, pi); 
    //}




}
