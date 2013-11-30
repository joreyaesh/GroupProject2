package edu.cmich.cps396m.geosilence;

import java.io.Serializable;

import android.media.AudioManager;

public class Rule implements Serializable {
	/**
	 * increment every time a change is made to this class structure
	 */
	private static final long serialVersionUID = 5;

	private String name;

	private double lat;
	private double lng;
	private int radius;
	private int mode;
	private boolean[] weekdays;
	private int startTime;
	private int endTime;

	private boolean active;

	@Override
	public String toString() {
		return "Rule [name=" + name + ", lat=" + lat + ", lng=" + lng + ", radius=" + radius + ", mode=" + mode
				+ ", weekdays=" + weekdays + ", startTime=" + startTime + ", endTime=" + endTime + ", active=" + active
				+ "]";
	}

	/**
	 * creating a rule object with default values
	 */
	public Rule() {
		this.name = "";
		this.lat = 0;
		this.lng = 0;
		this.radius = 0;
		this.mode = AudioManager.RINGER_MODE_NORMAL;
		boolean[] tmp = { true, true, true, true, true, true, true };
		this.weekdays = tmp;
		this.startTime = 0;
		this.endTime = 0;
		this.active = true;
	}
	
	
	public Rule(String name, double lat, double lng, int radius, int mode, boolean[] weekdays, int startTime,
			int endTime, boolean active) {
		this.name = name;
		this.lat = lat;
		this.lng = lng;
		this.radius = radius;
		this.mode = mode;
		this.weekdays = weekdays;
		this.startTime = startTime;
		this.endTime = endTime;
		this.active = active;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lan) {
		this.lng = lan;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public boolean[] getWeekdays() {
		return weekdays;
	}

	public void setWeekdays(boolean[] weekdays) {
		this.weekdays = weekdays;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
