package edu.cmich.cps396m.geosilence;

import java.io.Serializable;

public class Rule implements Serializable{
	/**
	 * increment every time a change is made to this class structure
	 */
	private static final long serialVersionUID = 3;
	
	private String name;
	
	private double lat;
	private double lan;
	private double radius;	
	private int mode;	
	private boolean[] weekdays;
	private int startTime;
	private int endTime;
	
	private boolean active;

	@Override
	public String toString() {
		return "Rule [name=" + name + ", lat=" + lat + ", lan=" + lan
				+ ", radius=" + radius + ", mode=" + mode + ", weekdays="
				+ weekdays + ", startTime=" + startTime + ", endTime="
				+ endTime + ", active=" + active + "]";
	}

	public Rule(String name, double lat, double lan, double radius, int mode,
			boolean[] weekdays, int startTime, int endTime, boolean active) {
		this.name = name;
		this.lat = lat;
		this.lan = lan;
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

	public double getLan() {
		return lan;
	}

	public void setLan(double lan) {
		this.lan = lan;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
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
