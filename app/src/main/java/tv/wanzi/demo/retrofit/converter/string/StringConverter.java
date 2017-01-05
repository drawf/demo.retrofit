package tv.wanzi.demo.retrofit.converter.string;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by drawf on 17/1/5.
 * ------------------------------
 */

public class StringConverter implements Converter<ResponseBody, String> {

//    T convert(F value) throws IOException;
//    实现从 F(from) 到 T(to) 的转换

    @Override
    public String convert(ResponseBody value) throws IOException {
        return value.string();
    }
}
