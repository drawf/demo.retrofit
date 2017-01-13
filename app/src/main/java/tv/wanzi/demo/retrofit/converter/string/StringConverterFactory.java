package tv.wanzi.demo.retrofit.converter.string;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by drawf on 17/1/5.
 * ------------------------------
 */

public class StringConverterFactory extends Converter.Factory {

    public static StringConverterFactory create() {
        return new StringConverterFactory();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (type == String.class) {//判断是否是要处理的类型
            return new StringResponseBodyConverter();//这里创建具体的Converter
        }
        return null;//不能处理就返回null
    }

}
