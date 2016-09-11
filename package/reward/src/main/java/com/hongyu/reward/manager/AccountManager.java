package com.hongyu.reward.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.fw.zycoder.http.callback.DataCallback;
import com.fw.zycoder.utils.GlobalConfig;
import com.fw.zycoder.utils.SPUtil;
import com.hongyu.reward.R;
import com.hongyu.reward.config.Constants;
import com.hongyu.reward.http.ResponesUtil;
import com.hongyu.reward.model.BaseModel;
import com.hongyu.reward.model.LoginModel;
import com.hongyu.reward.model.TokenModel;
import com.hongyu.reward.request.CommonCallback;
import com.hongyu.reward.request.FindPwdRequestBuilder;
import com.hongyu.reward.request.GetAuthCodeRequestBuilder;
import com.hongyu.reward.request.GetTokenRequestBuilder;
import com.hongyu.reward.request.LoginRequestBuilder;
import com.hongyu.reward.request.RegisterRequestBuilder;
import com.hongyu.reward.request.SetPwdRequestBuilder;
import com.hongyu.reward.ui.fragment.WelcomeFragment;
import com.hongyu.reward.utils.T;


/**
 * Created by zhangyang131 on 16/9/8.
 */
public class AccountManager {
  private static AccountManager instance;
  private String mtoken;

  public static AccountManager getInstance() {
    if (instance == null) {
      instance = new AccountManager();
    }
    return instance;
  }

  public static AccountManager getInstance(Context context) {
    if (GlobalConfig.getAppContext() == null) {
      GlobalConfig.setAppContext(context);
    }
    if (instance == null) {
      instance = new AccountManager();
    }
    return instance;
  }

  public boolean isLogin() {
    return false;
  }

  public boolean needToShowWelcome() {
    int v = SPUtil.getInt(Constants.Pref.GUIDE_KEY, 0);
    if (WelcomeFragment.GUIDE_VERSION > v) { // 如果有新的guide页面就应该看看新的
      SPUtil.putInt(Constants.Pref.GUIDE_KEY, WelcomeFragment.GUIDE_VERSION);
      return true;
    }
    return true;
  }

  public void login(String account, String pwd, final LoginRequestBuilder.LoginCallback callback) {
    LoginRequestBuilder builder = new LoginRequestBuilder(account, pwd);
    builder.setDataCallback(new DataCallback<LoginModel>() {
      @Override
      public void onDataCallback(final LoginModel data) {
        if (ResponesUtil.checkModelCodeOK(data)) {
          callback.loginSuccess(data.getData());
          saveUserInfo(data.getData());
        } else {
          callback.loginFailed(ResponesUtil.getErrorMsg(data));
        }
      }
    });
    builder.build().submit();
  }

  public void register(String phone, String repwd, String code,
      final RegisterRequestBuilder.CallBack callBack) {
    final RegisterRequestBuilder builder = new RegisterRequestBuilder(phone, code, repwd);
    builder.setDataCallback(new DataCallback<BaseModel>() {
      @Override
      public void onDataCallback(BaseModel data) {
        if (ResponesUtil.checkModelCodeOK(data)) {
          callBack.success();
        } else {
          callBack.failed(ResponesUtil.getErrorMsg(data));
        }
      }
    });
    builder.build().submit();
  }



  /**
   * 获取本地token
   *
   * @auther jiawenze
   * @tags @return
   * @since 2016-7-9 上午7:38:49
   */
  public String getTokenLocation() {
    if (TextUtils.isEmpty(mtoken)) {
      SharedPreferences pref = GlobalConfig.getAppContext()
          .getSharedPreferences(Constants.Pref.USER_INFO, Context.MODE_PRIVATE);
      mtoken = pref.getString(Constants.Pref.TOKEN, "");
    }
    return mtoken;
  }

  public void getToken() {
    GetTokenRequestBuilder builder = new GetTokenRequestBuilder();
    builder.setDataCallback(new DataCallback<TokenModel>() {
      @Override
      public void onDataCallback(TokenModel data) {
        if (ResponesUtil.checkModelCodeOK(data)) {
          saveToken(data.getToken());
        } else {
          T.show(R.string.get_token_error);
        }
      }
    });
    builder.build().submit();
  }

  public void getCode(String phone, final GetAuthCodeRequestBuilder.CallBack callBack) {
    GetAuthCodeRequestBuilder builder = new GetAuthCodeRequestBuilder(phone);
    builder.setDataCallback(new DataCallback<BaseModel>() {
      @Override
      public void onDataCallback(BaseModel data) {
        if (ResponesUtil.checkModelCodeOK(data)) {
          callBack.success();
          T.show(R.string.auth_tip);
        } else {
          callBack.failed(ResponesUtil.getErrorMsg(data));
        }
      }
    });
    builder.build().submit();

  }

  public void findPwdVerfiy(String phoneNum, String authCode, final CommonCallback callback) {
    FindPwdRequestBuilder builder = new FindPwdRequestBuilder(phoneNum, authCode);
    builder.setDataCallback(new DataCallback<BaseModel>() {
      @Override
      public void onDataCallback(BaseModel data) {
        if(ResponesUtil.checkModelCodeOK(data)){
          callback.success();
        }else{
          callback.failed(ResponesUtil.getErrorMsg(data));
        }
      }
    });
    builder.build().submit();

  }

  public void setPwd(String phoneNum, String pwd, final CommonCallback callback){
    SetPwdRequestBuilder builder = new SetPwdRequestBuilder(phoneNum, pwd);
    builder.setDataCallback(new DataCallback<BaseModel>() {
      @Override
      public void onDataCallback(BaseModel data) {
        if(ResponesUtil.checkModelCodeOK(data)){
          callback.success();
        }else{
          callback.failed(ResponesUtil.getErrorMsg(data));
        }
      }
    });
    builder.build().submit();

  }

  private void saveToken(String token) {
    // JPushInterface.init(mContext);
    // JPushInterface.setAlias(mContext, token, null);
    SharedPreferences pref = GlobalConfig.getAppContext()
        .getSharedPreferences(Constants.Pref.USER_INFO, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = pref.edit();
    editor.putString(Constants.Pref.TOKEN, token);
    editor.commit();
    this.mtoken = token;
  }

  private void saveUserInfo(LoginModel.UserInfo user) {
    /**
     * 将个人信息持久化到本地
     */
    SharedPreferences pref = GlobalConfig.getAppContext()
        .getSharedPreferences(Constants.Pref.USER_INFO, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = pref.edit();
    editor.putString("user_id", user.getUser_id());
    editor.putString("username", user.getNickname());
    editor.putString("avatar", user.getAvatar());
    editor.putString("phone", user.getPhone());
    editor.putInt("gender", user.getGender());
    editor.putFloat("score", user.getScore());
    editor.putFloat("cash", user.getCash());
    editor.putFloat("lock_cash", user.getLock_cash());
    editor.commit();
  }


}
