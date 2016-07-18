package com.fw.zycoder.appbase.utils;

import android.view.View;

import com.fw.zycoder.appbase.utils.TipsViewUtil.TipsType;
import com.fw.zycoder.appbase.view.AppEmptyView;
import com.fw.zycoder.appbase.view.AppEmptyView.OnEmptyRefreshListener;

/**
 * @author liuxu34@wanda.cn (Liu Xu)
 */
public class EmptyTipsUtil {

  public static void showEmptyTips(View targetView, OnEmptyRefreshListener refreshListener) {
    View tipsView = TipsViewUtil.showTipsView(targetView, TipsViewUtil.TipsType.EMPTY);
    if (tipsView instanceof AppEmptyView) {
      ((AppEmptyView) tipsView).setOnRefreshListener(refreshListener);
      ((AppEmptyView) tipsView).show();
    }
  }

  public static void showEmptyTips(View targetView, String emptyText,
      OnEmptyRefreshListener refreshListener) {
    View tipsView = TipsViewUtil.showTipsView(targetView, TipsType.EMPTY);
    if (tipsView instanceof AppEmptyView) {
      ((AppEmptyView) tipsView).setOnRefreshListener(refreshListener);
      ((AppEmptyView) tipsView).setEmptyText(emptyText);
      ((AppEmptyView) tipsView).show();
    }
  }

  public static void showEmptyTips(View targetView, String emptyText, int emptyResId,
      OnEmptyRefreshListener refreshListener) {
    View tipsView = TipsViewUtil.showTipsView(targetView, TipsType.EMPTY);
    if (tipsView instanceof AppEmptyView) {
      ((AppEmptyView) tipsView).setOnRefreshListener(refreshListener);
      ((AppEmptyView) tipsView).setEmptyText(emptyText);
      ((AppEmptyView) tipsView).setEmptyImage(emptyResId);
      ((AppEmptyView) tipsView).show();
    }
  }

  public static void hideEmptyTips(View targetView) {
    TipsViewUtil.hideTipsView(targetView, TipsType.EMPTY);
  }
}
