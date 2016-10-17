package vip.help.gbb.activity.base;

import android.app.Application;

import com.fw.zycoder.errorpage.CustomActivityOnCrash;
import com.fw.zycoder.utils.GlobalConfig;
import com.fw.zycoder.utils.Log;
import com.squareup.leakcanary.LeakCanary;

import vip.help.gbb.BuildConfig;

/**
 * Created by zhangyang131 on 16/6/12.
 */
public class BaseApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    long t1 = System.currentTimeMillis();
    initLeakCanary();
    initErrorPage();
    initGlobalConfig();
    initLog();
    Log.i("time:"+(System.currentTimeMillis() - t1));
  }

  private void initLeakCanary() {
    LeakCanary.install(this);
  }


  /**
   * 初始化log类
   */
  private void initLog() {
    Log.setIsPrintLog(BuildConfig.IS_SHOW_LOG);
  }

  /**
   * 初始化全局变量
   */
  private void initGlobalConfig() {
    GlobalConfig.setAppContext(this);
  }

  /**
   * 初始化崩溃页面
   */
  private void initErrorPage() {
    CustomActivityOnCrash.install(this);
    // 程序在后台崩溃是否显示错误页面
    CustomActivityOnCrash.setLaunchErrorActivityWhenInBackground(false);
    // 是否显示错误详细信息
    CustomActivityOnCrash.setShowErrorDetails(BuildConfig.DEBUG);
    // 是否打印log日志
    CustomActivityOnCrash.setShowErrorLog(BuildConfig.DEBUG);
  }
}
