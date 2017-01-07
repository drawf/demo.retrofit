package tv.wanzi.demo.retrofit.adapter.custom;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * Created by drawf on 17/1/7.
 * ------------------------------
 */

public class CustomCallAdapterFactory extends CallAdapter.Factory {

    public static CustomCallAdapterFactory create() {
        return new CustomCallAdapterFactory();
    }

    /*在本例中，
    returnType为CustomCall<R>，
    getRawType(returnType)为CustomCall.class，
    responseType为R的具体类型
    */
    @Override
    public CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) != CustomCall.class) {//检查返回的原始类型是否为CustomCall
            return null;
        }

        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalArgumentException(
                    "Call return type must be parameterized as Call<Foo> or Call<? extends Foo>");
        }
        Type responseType = getParameterUpperBound(0, (ParameterizedType) returnType);
        return new CustomCallAdapter(responseType);
    }

    /**
     * 自定义的Call适配器，作用为将返回的Call<R> 转化为 CustomCall<R>
     */
    static class CustomCallAdapter implements CallAdapter<CustomCall<?>> {

        private Type responseType;

        public CustomCallAdapter(Type responseType) {
            this.responseType = responseType;
        }

        @Override
        public Type responseType() {
            return this.responseType;
        }

        @Override
        public <R> CustomCall<R> adapt(Call<R> call) {
            return new CustomCall(call);
        }
    }

    /**
     * 自定义返回类型
     * @param <R>
     */
    public static class CustomCall<R> {
        private Call<R> call;

        public CustomCall(Call<R> call) {
            this.call = call;
        }

        /**
         * 同步请求返回结果
         *
         * @return
         * @throws IOException
         */
        public R getResult() throws IOException {
            return this.call.execute().body();
        }
    }


}
