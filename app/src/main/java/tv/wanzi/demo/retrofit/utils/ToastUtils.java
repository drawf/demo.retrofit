package tv.wanzi.demo.retrofit.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

import tv.wanzi.demo.retrofit.MyApplication;

/**
 * Created by drawf on 17/1/1.
 * ------------------------------
 */
public class ToastUtils {
    private static Context sContext = MyApplication.getContext();
    private static Handler sHandler = new Handler(Looper.getMainLooper());
    private static Toast sToast;

    public static void show(final String message, final int duration) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                if (sToast == null) {
                    sToast = Toast.makeText(sContext, message, duration);
                } else {
                    sToast.setText(message);
                    sToast.setDuration(duration);
                }
                sToast.setGravity(Gravity.CENTER, 0, 0);
                sToast.show();
            }
        });
    }

    public static void show(String message) {
        show(message, Toast.LENGTH_SHORT);
    }

    public static void show(int resId) {
        show(sContext.getString(resId));
    }

    public static void show(int resId, int duration) {
        show(sContext.getString(resId), duration);
    }
}
