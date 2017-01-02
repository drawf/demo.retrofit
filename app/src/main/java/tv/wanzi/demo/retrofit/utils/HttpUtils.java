package tv.wanzi.demo.retrofit.utils;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;

/**
 * Created by drawf on 17/1/2.
 * ------------------------------
 */

public class HttpUtils {

    public static String requestBody2String(RequestBody requestBody) throws IOException {
        StringBuilder sb = new StringBuilder("Request Body->");
        okio.Buffer buffer = new okio.Buffer();
        requestBody.writeTo(buffer);
        Charset charset = Charset.forName("UTF-8");
        MediaType contentType = requestBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(charset);
        }
        if (isPlaintext(buffer)) {
            sb.append(buffer.readString(charset));
            sb.append(" (Content-Type = ").append(contentType.toString()).append(",")
                    .append(requestBody.contentLength()).append("-byte body)");
        } else {
            sb.append(" (Content-Type = ").append(contentType.toString())
                    .append(",binary ").append(requestBody.contentLength()).append("-byte body omitted)");
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
