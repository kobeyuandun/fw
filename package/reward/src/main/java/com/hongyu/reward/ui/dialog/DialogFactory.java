package com.hongyu.reward.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.fw.zycoder.utils.DisplayUtil;
import com.hongyu.reward.R;

public class DialogFactory {

  /**
   * 输入就餐人数
   *
   * @auther jiawenze
   * @since 2016-7-12 上午5:09:15
   * @tags @param context
   * @tags @param listen
   */
  public static void showNumInputView(final Context context,
      final OnWhichBackStringListener listen) {
    LayoutInflater factory = LayoutInflater.from(context);
    final View view = factory.inflate(R.layout.dialog_select_input_num, null);

    final Dialog builder = new AlertDialog.Builder(context, R.style.DialogStyle).create();
    builder.show();
    builder.setContentView(view);

    // 软键盘把dialog整个推上去
    builder.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    builder.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    Window window = builder.getWindow();
    window.setGravity(Gravity.BOTTOM);
    window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    window.setWindowAnimations(R.style.dialog_sheet_window_anim);
    WindowManager.LayoutParams lp = window.getAttributes();
    lp.width = DisplayUtil.getScreenWidth(context);
    lp.gravity = Gravity.BOTTOM;
    window.setAttributes(lp);

    InputMethodManager imm =
        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);


    final EditText content = (EditText) view.findViewById(R.id.content);
    View ok = view.findViewById(R.id.ok);
    View cancel = view.findViewById(R.id.cancel);
    ok.setOnClickListener(new android.view.View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String num = content.getText().toString().trim();
        builder.dismiss();
        listen.onConfirmClick(new String[] {num});
      }
    });

    cancel.setOnClickListener(new android.view.View.OnClickListener() {
      @Override
      public void onClick(View v) {
        builder.dismiss();
      }
    });

  }


  /**
   * 设置悬赏金额
   * 
   * @param context
   * @param listen
   */
  public static void showPriceInputView(Context context, final OnWhichBackStringListener listen) {
    LayoutInflater factory = LayoutInflater.from(context);
    final View view = factory.inflate(R.layout.select_input_price, null);

    final Dialog builder = new AlertDialog.Builder(context, R.style.DialogStyle).create();
    builder.show();
    builder.setContentView(view);

    builder.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    builder.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    Window window = builder.getWindow();
    window.setGravity(Gravity.BOTTOM);
    window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    window.setWindowAnimations(R.style.dialog_sheet_window_anim);

    final EditText content = (EditText) view.findViewById(R.id.content);
    View ok = view.findViewById(R.id.ok);
    View cancel = view.findViewById(R.id.cancel);

    WindowManager windowManager = builder.getWindow().getWindowManager();
    Display display = windowManager.getDefaultDisplay();
    WindowManager.LayoutParams lp = builder.getWindow().getAttributes();
    lp.width = DisplayUtil.getScreenWidth(context);
    builder.getWindow().setAttributes(lp);

    InputMethodManager imm =
        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

    ok.setOnClickListener(new android.view.View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String num = content.getText().toString().trim();
        builder.dismiss();
        listen.onConfirmClick(new String[] {num});
      }
    });

    cancel.setOnClickListener(new android.view.View.OnClickListener() {
      @Override
      public void onClick(View v) {
        builder.dismiss();
      }
    });

  }

  public interface OnWhichBackStringListener {
    void onConfirmClick(String[] content);
  }

}
