package com.hongyu.reward.ui.fragment;

import android.content.DialogInterface;
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
import com.hongyu.reward.model.NoticeEvent;
import com.hongyu.reward.model.OrderInfoModel;
import com.hongyu.reward.model.OrderModel;
import com.hongyu.reward.model.ReceiveOrderInfo;
import com.hongyu.reward.request.GetOrderInfoRequestBuilder;
import com.hongyu.reward.request.ReceiveOrderRequestBuilder;
import com.hongyu.reward.ui.activity.OrderDetailActivity;
import com.hongyu.reward.ui.activity.order.ReceiveWaitActivity;
import com.hongyu.reward.ui.dialog.DialogFactory;
import com.hongyu.reward.utils.T;
import com.hongyu.reward.widget.FiveStarSingle;
import com.hongyu.reward.widget.NetImageView;
import com.hongyu.reward.widget.RoundImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
  private TextView mAddress;
  private TextView mTvNum;
  private TextView mTvPrice;
  private RoundImageView mIvHeader;
  private TextView mTvName;
  private TextView mTvOrderNum;
  private TextView mTvGcr;
  private TextView mTip;
  private FiveStarSingle mScoreView;
  private View mReceiveBtn;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getData();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
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
        if (!isAdded()) {
          return;
        }
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
    mTvGcr.setText("好评率:" + (TextUtils.isEmpty(order.getGcr()) ? "0%" : order.getGcr()));
    mTvName.setText(order.getNickname());
    mTvOrderNum.setText("成交:" + order.getOrder_num() + "单");
    mIvHeader.loadNetworkImageByUrl(order.getHead_img());
    mIvShop.loadNetworkImageByUrl(order.getImg());
    mAddress.setText("地址："+order.getShop_address());
    mScoreView.setData(order.getGcr(), false);
    mTvNum.setText(order.getUsernum() + "人");
    if (order.getStatus() != OrderModel.STATUS_PENDING_RECEIVE) { // 该订单已经被人领取
      mReceiveBtn.setEnabled(false);
      if (order.getStatus() == OrderModel.STATUS_INVALID
          || order.getStatus() == OrderModel.STATUS_CANCEL
          || order.getStatus() == OrderModel.STATUS_APPEND) {
        mTip.setText("*抱歉，该任务已经取消，请返回列表获取最新任务。");
      } else {
        mTip.setText("*抱歉，该任务已被人领取，请返回列表获取最新任务。");
      }
    } else {
      mReceiveBtn.setEnabled(true);
      mReceiveBtn.setOnClickListener(OrderDetailFragment.this);
      mTip.setText("*我们为悬赏人保留最长10分钟的考虑时间，请您耐心等待。");
    }
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
    mAddress = (TextView) mContentView.findViewById(R.id.address);
    mTvGcr = (TextView) mContentView.findViewById(R.id.gcr);
    mTip = (TextView) mContentView.findViewById(R.id.tip);
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
        if (!isAdded()) {
          return;
        }
        if (ResponesUtil.checkModelCodeOK(data)) {
          T.show("领取成功");
          EventBus.getDefault().post(new NoticeEvent(NoticeEvent.ORDER_STATUS_CHANGED));
          setLoadingViewCancelable(true);
          setLoadingCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
              getActivity().finish();
            }
          });
          showLoadingView("待悬赏人确认");
        } else {
          T.show(ResponesUtil.getErrorMsg(data));
        }
      }
    });
    builder.build().submit();
  }

  /**
   * 悬赏人确认接受该订单的接单请求
   * 
   * @param order 订单信息
   */
  @Subscribe
  public void onEventMainThread(ReceiveOrderInfo order) {
    if (!isAdded()) {
      return;
    }
    ReceiveWaitActivity.launch(getActivity(), order.getOrderId());
    getActivity().finish();
  }
}
