package edu.cmich.cps396m.geosilence.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class PollReceiver extends BroadcastReceiver {
	private static int minutes = 10;
	private static final int PERIOD = 1000 * 60 * minutes; // min minutes
	private static final int INITIAL_DELAY = 1000 * 5; // 5 seconds

	@Override
	public void onReceive(Context ctxt, Intent i) {
		Log.d("GS", "Alarm received");
		if (i.getAction() == null) {
			WakefulIntentService.sendWakefulWork(ctxt,
					SuperBackroundAgent.class);
		} else {
			scheduleAlarms(ctxt);
		}
	}

	public static void scheduleAlarms(Context ctxt) {
		AlarmManager mgr = (AlarmManager) ctxt.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(ctxt, PollReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(ctxt, 0, i, 0);

		mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()
				+ INITIAL_DELAY, PERIOD, pi);
		Log.d("GS", "Alarms scheduled");
	}

	public void changeIntveral(int minutes) {
		this.minutes = minutes;
	}
}
