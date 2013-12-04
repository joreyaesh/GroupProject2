package edu.cmich.cps396m.geosilence.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class PollReceiver extends BroadcastReceiver {
	private static final int INITIAL_DELAY = 1000 * 5; // 5 seconds
//	private Context ctxt;

	@Override
	public void onReceive(Context ctxt, Intent i) {
		// Log.d("GS", "Alarm received");
		if (i.getAction() == null) {
			WakefulIntentService.sendWakefulWork(ctxt,
					SuperBackroundAgent.class);
		} else {
			scheduleAlarms(ctxt);
		}
	}

	public static void scheduleAlarms(Context ctxt) {
		//this.ctxt = ctxt;
		AlarmManager mgr = (AlarmManager) ctxt
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(ctxt, PollReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(ctxt, 0, i, 0);

		mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime() + INITIAL_DELAY, getPeriodMillis(ctxt), pi);
		// Log.d("GS", "Alarms scheduled");

	}
	
	private static long getPeriodMillis(Context ctxt) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctxt);
		return 1000 * 60 * Integer.parseInt(pref.getString("locFreq", "10"));
	}
	
//
//	public void SetOrDeleteAlarm(int Interval) {
//
//		if (Interval != 0) {
//			AlarmManager mgr = (AlarmManager) ctxt
//					.getSystemService(Context.ALARM_SERVICE);
//			Intent i = new Intent(ctxt, PollReceiver.class);
//			PendingIntent pi = PendingIntent.getBroadcast(ctxt, 0, i, 0);
//
//			mgr.cancel(pi);
//
//			mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//					SystemClock.elapsedRealtime() + INITIAL_DELAY, PERIOD, pi);
//		}
//
//		else {
//			AlarmManager mgr = (AlarmManager) ctxt
//					.getSystemService(Context.ALARM_SERVICE);
//			Intent i = new Intent(ctxt, PollReceiver.class);
//			PendingIntent pi = PendingIntent.getBroadcast(ctxt, 0, i, 0);
//
//			mgr.cancel(pi);
//		}
//
//	}
}
