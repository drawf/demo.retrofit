package tv.wanzi.demo.retrofit.interceptor;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import tv.wanzi.demo.retrofit.BuildConfig;
import tv.wanzi.demo.retrofit.utils.LogUtils;

/**
 * Created by drawf on 17/1/7.
 * ------------------------------
 */

public class LoggingInterceptor implements Interceptor {
    private static final String SEPARATOR = " ⇢ ";
    private LOG eLog;

    public enum LOG {
        NONE, ALL, DEFAULT
    }

    public static LoggingInterceptor getInstance() {
        return new LoggingInterceptor(LOG.DEFAULT);
    }

    public static LoggingInterceptor getInstance(LOG eLog) {
        return new LoggingInterceptor(eLog);
    }

    public LoggingInterceptor(LOG eLog) {
        this.eLog = eLog;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originRequest = chain.request();

        long t1 = System.nanoTime();
        Response originResponse = chain.proceed(originRequest);
        long t2 = System.nanoTime();

        if (BuildConfig.DEBUG && eLog != LOG.NONE) {
            double time = (t2 - t1) / 1e6d;

            //url ⇢ http://xxx
            if (eLog == LOG.DEFAULT) {
                String format = "method%s%s\nurl%s%s\nbody%s%s\ntime%s%s\n" +
                        "response code%s%s\nresponse body%s%s\n";

                String message = String.format(format,
                        SEPARATOR, originRequest.method(),
                        SEPARATOR, originRequest.url(),
                        SEPARATOR, requestBody2String(originRequest.body()),
                        SEPARATOR, time,
                        SEPARATOR, originResponse.code(),
                        SEPARATOR, responseBody2String(originResponse.body()));
                LogUtils.v(message);
            }

            if (eLog == LOG.ALL) {
                String format = "method%s%s\nurl%s%s\nheaders%s%s\nbody%s%s\ntime%s%s\n" +
                        "response code%s%s\nresponse headers%s%s\nresponse body%s%s\n";

                String message = String.format(format,
                        SEPARATOR, originRequest.method(),
                        SEPARATOR, originRequest.url(),
                        SEPARATOR, originRequest.headers(),
                        SEPARATOR, requestBody2String(originRequest.body()),
                        SEPARATOR, time,
                        SEPARATOR, originResponse.code(),
                        SEPARATOR, originResponse.headers(),
                        SEPARATOR, responseBody2String(originResponse.body()));
                LogUtils.v(message);
            }

        }
        return originResponse;
    }

    private static String requestBody2String(RequestBody requestBody) throws IOException {
        if (requestBody == null) return "this request has no request body.";

        StringBuilder sb = new StringBuilder();
        okio.Buffer buffer = new okio.Buffer();
        requestBody.writeTo(buffer);

        Charset charset = Charset.forName("UTF-8");
        MediaType contentType = requestBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(charset);
        }

        if (isPlaintext(buffer)) {
            sb.append(buffer.readString(charset));
        } else {
            sb.append(requestBody.contentLength()).append("-byte binary body omitted)");
        }
        return sb.toString();
    }

    private static String responseBody2String(ResponseBody responseBody) throws IOException {
        if (responseBody == null) return "this request has no response body.";

        StringBuilder sb = new StringBuilder();

        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer().clone();

        Charset charset = Charset.forName("UTF-8");
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(charset);
        }

        if (isPlaintext(buffer)) {
            sb.append(buffer.readString(charset));
        } else {
            sb.append(responseBody.contentLength()).append("-byte binary body omitted)");
        }
        return sb.toString();
    }

    private static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }
}
