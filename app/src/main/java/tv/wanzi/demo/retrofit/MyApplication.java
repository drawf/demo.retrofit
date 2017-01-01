package tv.wanzi.demo.retrofit;

import android.app.Application;

/**
 * Created by drawf on 17/1/1.
 * ------------------------------
 */

public class MyApplication extends Application {
    private static MyApplication sInstance;

    public MyApplication() {
        sInstance = this;
    }

    public static MyApplication getInstance() {
        return sInstance;
    }
}
