package com.hongyu.reward.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.fw.zycoder.utils.GlobalConfig;
import com.fw.zycoder.utils.Log;
import com.hongyu.reward.R;
import com.hongyu.reward.config.Constants;
import com.hongyu.reward.utils.T;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by zhangyang131 on 16/10/13.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
  public static final String TAG = WXEntryActivity.class.getSimpleName();
  private IWXAPI api;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.i(TAG, "onCreate");
    api = WXAPIFactory.createWXAPI(this, Constants.WX.AppID, false);
    api.handleIntent(getIntent(), this);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Log.i(TAG, "onNewIntent");
    setIntent(intent);
    api.handleIntent(intent, this);
  }

  @Override
  public void onReq(BaseReq baseReq) {
    Log.i(TAG, "onReq");
    this.finish();
  }

  @Override
  public void onResp(BaseResp resp) {
    Log.i(TAG, "onResp");
    int result = 0;
    switch (resp.errCode) {
      case BaseResp.ErrCode.ERR_OK:
        result = R.string.errcode_success;
        break;
      case BaseResp.ErrCode.ERR_USER_CANCEL:
        result = R.string.errcode_cancel;
        break;
      case BaseResp.ErrCode.ERR_AUTH_DENIED:
        result = R.string.errcode_deny;
        break;
      default:
        result = R.string.errcode_unknown;
        break;
    }
    Log.i(TAG, "resp:" + resp.errCode + "=+==" + resp.errStr + "====" + resp.transaction);
    T.show(result);
    this.finish();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Log.i(TAG, "onDestroy");
    api = null;
  }



  private static String buildTransaction(String type) {
    return (type == null)
            ? String.valueOf(System.currentTimeMillis())
            : type + System.currentTimeMillis();
  }

  public static void receiveShare(IWXAPI api, String shopName, int type) {
    WXWebpageObject webpage = new WXWebpageObject();
    webpage.webpageUrl = Constants.WX.share_app + "shop_name=" + shopName;
    WXMediaMessage msg = new WXMediaMessage(webpage);
    msg.title = "快来使用" + getAppName() + "吧~~~";
    msg.description =
            "我在 " + shopName + " 排队吃饭，通过维依悬赏APP把排队号给了更紧急，更需要的人，助人为乐，还小赚了一笔！下次需要的时候，我也用悬赏少排队~";
    // Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.send_music_thumb);
    // msg.thumbData = Util.bmpToByteArray(thumb, true);
    SendMessageToWX.Req req = new SendMessageToWX.Req();
    req.transaction = buildTransaction("webpage");
    req.message = msg;
    if (type == 1) {
      req.scene = SendMessageToWX.Req.WXSceneSession;
    } else if (type == 2) {
      req.scene = SendMessageToWX.Req.WXSceneTimeline;
    }
    api.sendReq(req);
  }

  /**
   *
   * @param order_id
   * @param save_seat
   * @param save_time
   * @param type 1分享微信， 2分享朋友圈
   */
  public static void publishShare(IWXAPI api, String order_id, String save_seat, String save_time,
                                  int type) {
    WXWebpageObject webpage = new WXWebpageObject();
    webpage.webpageUrl = Constants.WX.share_shop + "order_id=" + order_id + "&save_time="
            + save_time + "&save_seat=" + save_seat;
    WXMediaMessage msg = new WXMediaMessage(webpage);
    msg.title = "快来使用" + getAppName() + "吧~~~";
    msg.description = "我使用" + getAppName() + ", 节省了" + save_time + "分钟!从此吃饭不排队~~~~";
    // Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.send_music_thumb);
    // msg.thumbData = Util.bmpToByteArray(thumb, true);
    SendMessageToWX.Req req = new SendMessageToWX.Req();
    req.transaction = buildTransaction("webpage");
    req.message = msg;
    if (type == 1) {
      req.scene = SendMessageToWX.Req.WXSceneSession;
    } else if (type == 2) {
      req.scene = SendMessageToWX.Req.WXSceneTimeline;
    }

    api.sendReq(req);
  }

  private static String getAppName() {
    return GlobalConfig.getAppContext().getResources().getString(R.string.app_name_reward);
  }

  public static IWXAPI registWX(Context context) {
    IWXAPI api = WXAPIFactory.createWXAPI(context, Constants.WX.AppID, false);
    return api;
  }
}
