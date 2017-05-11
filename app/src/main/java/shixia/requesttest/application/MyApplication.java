package shixia.requesttest.application;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by ShiXiuwen on 2017/5/9.
 * <p>
 * Email:shixiuwen1991@yeah.net
 * Description:
 */

public class MyApplication extends Application {

    //声明全局context
    public static Context ctx;

    //初始化运行在UI线程中的handler
    public static Handler UIHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化全局context
        ctx = MyApplication.this;
    }
}
