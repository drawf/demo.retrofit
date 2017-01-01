package tv.wanzi.demo.retrofit.utils;

import android.util.Log;
import android.widget.Toast;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

import java.util.List;

import tv.wanzi.demo.retrofit.BuildConfig;

/**
 * Created by drawf on 17/1/1.
 * ------------------------------
 */
public class LogUtils {
    private static final String TAG = "wanzi";
    private static final boolean ENABLED = BuildConfig.DEBUG;
    private static final boolean STRICT = true;

    private static final int MESSAGE_MAX_LEN = 3600;
    private static final String SEPARATOR = " ⇢ ";

    private LogUtils() {
    }

    public static void trace() {
        log(Log.DEBUG, "TRACE()", null);
    }

    public static void trace(Object object) {
        String message = String.valueOf(object);
        log(Log.DEBUG, "TRACE(" + message + ")", null);
    }

    public static void trace(String format, Object... args) {
        String message = String.format(format, args);
        log(Log.DEBUG, "TRACE(" + message + ")", null);
    }

    public static void v(Throwable tr) {
        log(Log.VERBOSE, null, tr);
    }

    public static void v(Object object) {
        log(Log.VERBOSE, object, null);
    }

    public static void v(Object object, Throwable tr) {
        log(Log.VERBOSE, object, tr);
    }

    public static void d(Throwable tr) {
        log(Log.DEBUG, null, tr);
    }

    public static void d(Object object) {
        log(Log.DEBUG, object, null);
    }

    public static void d(Object object, Throwable tr) {
        log(Log.DEBUG, object, tr);
    }

    public static void i(Throwable tr) {
        log(Log.INFO, null, tr);
    }

    public static void i(Object object) {
        log(Log.INFO, object, null);
    }

    public static void i(Object object, Throwable tr) {
        log(Log.INFO, object, tr);
    }

    public static void w(Throwable tr) {
        log(Log.WARN, null, tr);
    }

    public static void w(Object object) {
        log(Log.WARN, object, null);
    }

    public static void w(Object object, Throwable tr) {
        log(Log.WARN, object, tr);
    }

    public static void e(Throwable tr) {
        log(Log.ERROR, null, tr);
    }

    public static void e(Object object) {
        log(Log.ERROR, object, null);
    }

    public static void e(Object object, Throwable tr) {
        log(Log.ERROR, object, tr);
    }

    public static void wtf(Throwable tr) {
        log(Log.ASSERT, null, tr);
    }

    public static void wtf(Object object) {
        log(Log.ASSERT, object, null);
    }

    public static void wtf(Object object, Throwable tr) {
        log(Log.ASSERT, object, tr);
    }

    private static String generateTrace() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String trace = stackTrace[5].toString();

        int begin = trace.indexOf('(');
        int end = trace.indexOf(')');
        String file = trace.substring(begin, end + 1);
        List<String> names = Splitter.on('.').splitToList(trace.substring(0, begin));
        int size = names.size();
        trace = names.get(size - 2) + "." + names.get(size - 1) + file;

        return trace;
    }

    private static String generateMessage(Object object) {
        String message = String.valueOf(object);
        message = CharMatcher.anyOf("\r\n").replaceFrom(message, " ");

        if (!STRICT && message.length() >= MESSAGE_MAX_LEN) {
            message = message.substring(0, MESSAGE_MAX_LEN) + "§";
        }

        return message;
    }

    private static void log(int level, Object o, Throwable tr) {
        if (!ENABLED) return;

        String trace = generateTrace();
        String toast = trace.split("\\(")[0];
        String message = generateMessage(o) + SEPARATOR + trace;

        if (tr != null) {
            message += "\n" + Log.getStackTraceString(tr);
        }

        switch (level) {
            case Log.VERBOSE:
                Log.v(TAG, message);
                break;
            case Log.DEBUG:
                Log.d(TAG, message);
                break;
            case Log.INFO:
                Log.i(TAG, message);
                break;
            case Log.WARN:
                Log.w(TAG, message);
                ToastUtils.show("Log.WARN\n" + toast, Toast.LENGTH_LONG);
                break;
            case Log.ERROR:
                Log.e(TAG, message);
                if (STRICT) {
                    throw new IllegalStateException(message);
                } else {
                    ToastUtils.show("Log.ERROR\n" + toast, Toast.LENGTH_LONG);
                }
                break;
            case Log.ASSERT:
                Log.wtf(TAG, message);
                ToastUtils.show("Log.ASSERT\n" + toast, Toast.LENGTH_LONG);
                break;
            default:
                throw new IllegalStateException();
        }
    }
}