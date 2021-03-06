package com.hongyu.reward.location;

import com.hongyu.reward.model.AppLocation;

public interface GetLocationListener {
  void onSuccess(AppLocation locationInfo);

  void onFailed(String msg);
}
