package tv.wanzi.demo.retrofit;

import android.app.Application;
import android.content.Context;

import java.io.IOException;

import tv.wanzi.demo.retrofit.utils.FileUtils;
import tv.wanzi.demo.retrofit.utils.LogUtils;

/**
 * Created by drawf on 17/1/1.
 * ------------------------------
 */

public class MainApplication extends Application {
    private static MainApplication sInstance;
    private static Context sContext;

    public static MainApplication getInstance() {
        return sInstance;
    }

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        sContext = this.getApplicationContext();
        init();
    }

    private void init() {
        try {
            FileUtils.copyAssetFile2EFD("image/abc.jpg");
        } catch (IOException e) {
            LogUtils.e(e);
        }
    }
}
