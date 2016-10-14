package com.hongyu.reward.ui.fragment.personal;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hongyu.reward.R;
import com.hongyu.reward.appbase.BaseLoadFragment;
import com.hongyu.reward.manager.AccountManager;
import com.hongyu.reward.model.LoginModel;
import com.hongyu.reward.utils.T;
import com.hongyu.reward.widget.CommonTextView;
import com.hongyu.reward.widget.RoundImageView;

/**
 * Created by zhangyang131 on 16/9/21.
 */
public class ScoreFragment extends BaseLoadFragment implements View.OnClickListener {

  private CommonTextView mCellScoreShop;
  private TextView mTvName;
  private RoundImageView mHeadImag;
  private TextView mTvScore;

  @Override
  protected void onStartLoading() {
    showLoadingView();
    AccountManager.getInstance().requestUserInfo(new AccountManager.GetUserInfoCallback() {
      @Override
      public void getUserInfoSuccess(LoginModel.UserInfo userInfo) {
        if (!isAdded()) {
          return;
        }
        dismissLoadingView();
        mTvName.setText(userInfo.getNickname());
        mTvScore.setText(String.valueOf(userInfo.getScore()));
        mHeadImag.loadNetworkImageByUrl(userInfo.getHead_img());
      }

      @Override
      public void getUserInfoFailed(String msg) {
        if (!isAdded()) {
          return;
        }
        dismissLoadingView();
        T.show(msg);
      }
    });
  }

  @Override
  protected void onInflated(View contentView, Bundle savedInstanceState) {
    initView();
  }

  private void initView() {
    mCellScoreShop = (CommonTextView) mContentView.findViewById(R.id.score_shop);
    mHeadImag = (RoundImageView) mContentView.findViewById(R.id.header_icon);
    mTvName = (TextView) mContentView.findViewById(R.id.name);
    mTvScore = (TextView) mContentView.findViewById(R.id.score);
    mCellScoreShop.setOnClickListener(this);
  }

  @Override
  protected int getLayoutResId() {
    return R.layout.activity_score_layout;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.score_shop:
        T.show(R.string.tip_to_open);
        break;
    }
  }
}
