package com.hongyu.reward.appbase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;


/**
 * Created by zhangyang131 on 16/9/8.
 */
public abstract class BaseSingleFragmentActivity extends AppBaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getSingleContentFragmentClass() != null) {
      mFragment = (BaseFragment) Fragment.instantiate(this, getSingleContentFragmentClass().getName(),
          getIntent().getExtras());
      if (mFragment != null) {
        replaceFragment(mFragment);
      }
    }
  }

  public BaseFragment getFragment(){
    return mFragment;
  }

  protected abstract Class<? extends Fragment> getSingleContentFragmentClass();
}
