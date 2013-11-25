/*
 * Copyright (C) 2013 47 Degrees, LLC
 *  http://47deg.com
 *  hello@47deg.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.fortysevendeg.swipelistview.sample.adapters;

import edu.cmich.cps396m.geosilence.Rule;

public class PackageItem {

    private String name;

    private double lat;
    private double lan;
    private double radius;
    private int mode;
    private boolean[] weekdays;
    private int startTime;
    private int endTime;

    private boolean active;

    public PackageItem(Rule rule) {
        this.name = rule.getName();
        this.lat = rule.getLat();
        this.lan = rule.getLan();
        this.radius = rule.getRadius();
        this.mode = rule.getMode();
        this.weekdays = rule.getWeekdays();
        this.startTime = rule.getStartTime();
        this.endTime = rule.getEndTime();
        this.active = rule.isActive();
    }

    @Override
    public String toString() {
        return "Rule [name=" + name + ", lat=" + lat + ", lan=" + lan
                + ", radius=" + radius + ", mode=" + mode + ", weekdays="
                + weekdays + ", startTime=" + startTime + ", endTime="
                + endTime + ", active=" + active + "]";
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
