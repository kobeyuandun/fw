package com.hongyu.reward.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.fw.zycoder.http.callback.DataCallback;
import com.hongyu.reward.R;
import com.hongyu.reward.appbase.BaseLoadFragment;
import com.hongyu.reward.http.ResponesUtil;
import com.hongyu.reward.model.BaseModel;
import com.hongyu.reward.model.OrderInfoModel;
import com.hongyu.reward.model.OrderModel;
import com.hongyu.reward.request.GetOrderInfoRequestBuilder;
import com.hongyu.reward.request.ReceiveOrderRequestBuilder;
import com.hongyu.reward.ui.activity.OrderDetailActivity;
import com.hongyu.reward.ui.activity.RewardStartActivity;
import com.hongyu.reward.ui.dialog.DialogFactory;
import com.hongyu.reward.utils.T;
import com.hongyu.reward.widget.FiveStarSingle;
import com.hongyu.reward.widget.NetImageView;
import com.hongyu.reward.widget.RoundImageView;

/**
 * 可领取任务详情页
 * Created by zhangyang131 on 16/9/19.
 */
public class OrderDetailFragment extends BaseLoadFragment implements View.OnClickListener {
  String shop_name;
  String shop_img;
  String order_id;
  String nickname;
  String price;
  String shop_id;
  String user_id;
  private NetImageView mIvShop;
  private TextView mTvShopName;
  private TextView mTvNum;
  private TextView mTvPrice;
  private RoundImageView mIvHeader;
  private TextView mTvName;
  private TextView mTvOrderNum;
  private TextView mTvGcr;
  private FiveStarSingle mScoreView;
  private View mReceiveBtn;

  private OrderModel order;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getData();
  }

  private void getData() {
    shop_name = getArguments().getString(OrderDetailActivity.SHOP_NAME);
    shop_img = getArguments().getString(OrderDetailActivity.SHOP_IMAGE);
    order_id = getArguments().getString(OrderDetailActivity.ORDER_ID);
    nickname = getArguments().getString(OrderDetailActivity.NICKNAME);
    price = getArguments().getString(OrderDetailActivity.PRICE);
    shop_id = getArguments().getString(OrderDetailActivity.SHOP_ID);
    user_id = getArguments().getString(OrderDetailActivity.USER_ID);
  }

  @Override
  protected void onStartLoading() {
    showLoadingView();
    GetOrderInfoRequestBuilder builder = new GetOrderInfoRequestBuilder(order_id);
    builder.setDataCallback(new DataCallback<OrderInfoModel>() {
      @Override
      public void onDataCallback(OrderInfoModel data) {
        mReceiveBtn.setOnClickListener(OrderDetailFragment.this);
        dismissLoadingView();
        if (ResponesUtil.checkModelCodeOK(data)) {
          refreshData(data.getData().getOrder());
        } else {
          T.show(ResponesUtil.getErrorMsg(data));
        }
      }
    });
    builder.build().submit();
  }

  private void refreshData(OrderModel order) {
    this.order = order;
    mTvGcr.setText("好评率:" + (TextUtils.isEmpty(order.getGcr()) ? "0%" : order.getGcr()));
    mTvName.setText(order.getNickname());
    mTvOrderNum.setText("成交:" + order.getOrder_num() + "单");
    float good = 0;
    try {
      good = Float.parseFloat(TextUtils.isEmpty(order.getGood()) ? "0" : order.getGood());
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    mScoreView.setData(good, false);
    mTvNum.setText(order.getUsernum() + "人");
  }

  @Override
  protected void onInflated(View contentView, Bundle savedInstanceState) {
    initView();
  }

  private void initView() {
    mIvShop = (NetImageView) mContentView.findViewById(R.id.image);
    mTvShopName = (TextView) mContentView.findViewById(R.id.shop_name);
    mTvNum = (TextView) mContentView.findViewById(R.id.num);
    mTvPrice = (TextView) mContentView.findViewById(R.id.price);
    mTvName = (TextView) mContentView.findViewById(R.id.name);
    mTvGcr = (TextView) mContentView.findViewById(R.id.gcr);
    mTvOrderNum = (TextView) mContentView.findViewById(R.id.order_num);
    mScoreView = (FiveStarSingle) mContentView.findViewById(R.id.my_score);
    mIvHeader = (RoundImageView) mContentView.findViewById(R.id.header_icon);
    mReceiveBtn = mContentView.findViewById(R.id.receive);
    mReceiveBtn.setOnClickListener(this);
    mTvShopName.setText(shop_name);
    mTvPrice.setText(price);
    mTvName.setText(nickname);
    mIvShop.loadNetworkImageByUrl(shop_img);
  }

  @Override
  protected int getLayoutResId() {
    return R.layout.activity_reward_detail_layout;
  }


  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.receive:
        showReceiveDialog();
        break;
    }
  }

  private void showReceiveDialog() {
    DialogFactory.showNumeralInputView(getActivity(), new DialogFactory.OnDialogActionListener() {
      @Override
      public void onFinish(String indexNum, String waitNum, String pNum) {
        receiveOrder(indexNum, waitNum, pNum);
      }
    });
  }

  private void receiveOrder(final String indexNum, final String waitNum, final String pNum) {
    ReceiveOrderRequestBuilder builder =
        new ReceiveOrderRequestBuilder(order_id, indexNum, waitNum, pNum);
    builder.setDataCallback(new DataCallback<BaseModel>() {
      @Override
      public void onDataCallback(BaseModel data) {
        if (ResponesUtil.checkModelCodeOK(data)) {
          T.show("领取成功");
          RewardStartActivity.launch(getActivity(), order.getShop_name(), order.getImg(),
              order.getOrder_id(), indexNum, waitNum, pNum);
          getActivity().finish();
        } else {
          T.show(ResponesUtil.getErrorMsg(data));
        }
      }
    });
    builder.build().submit();
  }
}
