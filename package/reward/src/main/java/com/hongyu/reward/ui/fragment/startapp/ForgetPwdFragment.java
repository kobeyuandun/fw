package com.hongyu.reward.ui.fragment.startapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fw.zycoder.http.callback.DataCallback;
import com.fw.zycoder.utils.MainThreadPostUtils;
import com.fw.zycoder.utils.PhoneNumberUtils;
import com.hongyu.reward.R;
import com.hongyu.reward.appbase.BaseLoadFragment;
import com.hongyu.reward.http.ResponesUtil;
import com.hongyu.reward.model.BaseModel;
import com.hongyu.reward.request.FindPwdRequestBuilder;
import com.hongyu.reward.request.GetAuthCodeRequestBuilder;
import com.hongyu.reward.ui.activity.RePwdAuthActivity;
import com.hongyu.reward.utils.InputUtil;
import com.hongyu.reward.utils.T;

/**
 * Created by zhangyang131 on 16/9/10.
 */
public class ForgetPwdFragment extends BaseLoadFragment implements View.OnClickListener {
  public static final String TIME = "time";
  public static final String MAX = "60";
  private EditText mPhone;
  private EditText mCode;
  private Button mBtnNext;
  private TextView mBtnGetCode;

  @Override
  protected void onInflated(View contentView, Bundle savedInstanceState) {
    initView();
  }

  private void initView() {
    mPhone = (EditText) mContentView.findViewById(R.id.phone);
    mCode = (EditText) mContentView.findViewById(R.id.code);
    mBtnGetCode = (TextView) mContentView.findViewById(R.id.get_code);
    mBtnNext = (Button) mContentView.findViewById(R.id.next);
    mBtnNext.setOnClickListener(this);
    mBtnGetCode.setOnClickListener(this);
  }

  @Override
  protected int getLayoutResId() {
    return R.layout.activity_repwd_auth_layout;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.next:
        next();
        break;
      case R.id.get_code:
        sendCode();
        break;
    }
  }

  private void next() {
    final String phone = mPhone.getText().toString().trim();
    String code = mCode.getText().toString().trim();
    if (TextUtils.isEmpty(phone)) {
      mPhone.setError(getString(R.string.login_phone_must_be_not_empty));
      return;
    }

    if (!PhoneNumberUtils.isPhoneNum(phone)) {
      mPhone.setError(getString(R.string.login_phone_not_match));
      return;
    }
    if (TextUtils.isEmpty(code)) {
      mCode.setError(getString(R.string.please_input_auth));
      return;
    }
    showLoadingView();
    FindPwdRequestBuilder builder = new FindPwdRequestBuilder(phone, code);
    builder.setDataCallback(new DataCallback<BaseModel>() {
      @Override
      public void onDataCallback(BaseModel data) {
        if (!isAdded()) {
          return;
        }
        dismissLoadingView();
        if (ResponesUtil.checkModelCodeOK(data)) {
          RePwdAuthActivity.launch(getActivity(), phone);
          getActivity().finish();
        } else {
          if (!isAdded()) {
            return;
          }
          T.show(ResponesUtil.getErrorMsg(data));
        }
      }
    });
    builder.build().submit();
  }

  private void sendCode() {
    String phone = mPhone.getText().toString().trim();
    String code = mCode.getText().toString().trim();
    if (!InputUtil.checkPhoneNum(mPhone)) {
      return;
    }

    showLoadingView();

    GetAuthCodeRequestBuilder builder = new GetAuthCodeRequestBuilder(phone);
    builder.setDataCallback(new DataCallback<BaseModel>() {
      @Override
      public void onDataCallback(BaseModel data) {
        if (!isAdded()) {
          return;
        }
        dismissLoadingView();
        if (ResponesUtil.checkModelCodeOK(data)) {
          startTiming();
          T.show(R.string.auth_tip);
        } else {
          T.show(ResponesUtil.getErrorMsg(data));
        }
      }
    });
    builder.build().submit();

  }

  private void startTiming() {
    mBtnGetCode.setText(getString(R.string.login_second, MAX));
    mBtnGetCode.setClickable(false);
    new Thread() {
      public void run() {
        int s = 60;
        while (s >= 0 && isAdded()) {
          resetSendBtnText(s);
          try {
            sleep(1000);
            s--;
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      };
    }.start();
  }

  private void resetSendBtnText(final int s) {
    MainThreadPostUtils.post(new Runnable() {
      @Override
      public void run() {
        if (s > 0) {
          mBtnGetCode.setText(getString(R.string.login_second, String.valueOf(s)));
          mBtnGetCode.setClickable(false);
        } else {
          mBtnGetCode.setText(R.string.login_get_auth);
          mBtnGetCode.setClickable(true);
        }
      }
    });
  }

  @Override
  protected void onStartLoading() {

  }
}
