package com.hongyu.reward.http;


import android.text.TextUtils;

import com.fw.zycoder.http.AppBaseHttpRequestBuilder;
import com.fw.zycoder.http.callback.DataCallback;
import com.fw.zycoder.http.request.GsonRequestBuilder;
import com.fw.zycoder.http.volley.ApiContext;
import com.fw.zycoder.utils.GlobalConfig;
import com.hongyu.reward.config.Constants;
import com.hongyu.reward.config.ResultCode;
import com.hongyu.reward.manager.AccountManager;
import com.hongyu.reward.model.BaseModel;

/**
 * Created by zhangyang131 on 16/9/8.
 */
public abstract class BaseHttpRequestBuilder<T> extends AppBaseHttpRequestBuilder<T> {
  private DataCallback<T> mNetWorkCallback = null;
  DataCallback<T> dataCallback = new DataCallback<T>() {
    @Override
    public void onDataCallback(T data) {
      if (null != BaseHttpRequestBuilder.this.mNetWorkCallback) {
        BaseHttpRequestBuilder.this.mNetWorkCallback.onDataCallback(data);
      }

      if (data != null && data instanceof BaseModel
              && ((BaseModel) data).getCode() == ResultCode.code_1004) { // 无效的登录状态
        com.hongyu.reward.utils.T.show("登录失效, 请重新登录");
        AccountManager.getInstance().logout();
      }
    }
  };

  @Override
  protected GsonRequestBuilder setMethod(int method) {
    return super.setMethod(method);
  }

  @Override
  protected ApiContext getApiContext() {
    return ApiContextManager.getInstance().getApiContext();
  }

  protected void setParams(Params params) {
    super.setParams(params);
    // 除了获取token的接口之外所有的请求都加上token参数
    if (!(Constants.Server.API_PREFIX + Constants.Server.API_GET_TOKEN).equals(getUrl())) {
      String token = GlobalConfig.getToken();
      params.put(Constants.Pref.TOKEN, token);
    }
  }

  protected void checkNullAndSet(Params params, String key, Object value) {
    if (value != null && !TextUtils.isEmpty(String.valueOf(value))) {
      params.put(key, value);
    }
  }

  public GsonRequestBuilder setDataCallback(DataCallback<T> callback) {
    this.mNetWorkCallback = callback;
    return super.setDataCallback(this.dataCallback);
  }

  @Override
  protected String getUrl() {
    return Constants.Server.API_PREFIX + getApiUrl();
  }

  protected abstract String getApiUrl();
}
