package tv.wanzi.demo.retrofit;

import android.app.Application;

import java.io.IOException;

import tv.wanzi.demo.retrofit.utils.FileUtils;
import tv.wanzi.demo.retrofit.utils.LogUtils;

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

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        try {
            FileUtils.copyAsset2EFD("image/abc.jpg");
        } catch (IOException e) {
            LogUtils.e(e);
        }
    }
}
