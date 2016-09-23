package com.hongyu.reward.model;

import java.text.DecimalFormat;

/**
 * Created by zhangyang131 on 16/9/22.
 */
public class AppLocation {
  private static final long MAX_PAST_TIME = 1000 * 60 * 5;
  private double latitude;
  private double longitude;
  private long setTime;

  public AppLocation(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.setTime = System.currentTimeMillis();
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public boolean isPast() {
    return System.currentTimeMillis() - setTime > MAX_PAST_TIME;
  }

  @Override
  public String toString() {
    DecimalFormat df = new DecimalFormat("###.#####");
    String location = df.format(latitude) + "," + df.format(longitude);
    return location;
  }
}
