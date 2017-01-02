package tv.wanzi.demo.retrofit.api;

import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import tv.wanzi.demo.retrofit.entity.User;

/**
 * Created by drawf on 17/1/2.
 * ------------------------------
 */

public interface MovieService {

    String BASE_URL = "https://api.douban.com/v2/movie/";

    @GET("top250")
    Call<JsonObject> getTopMovie(@Query(value = "start", encoded = true) int start, @Query("count") int count);

    /*可以替代其它请求方法

    String method(); 请求方法，必须大写
    String path() default ""; 请求路径
    boolean hasBody() default false; 是否有请求体*/
    @HTTP(method = "GET", path = "top250", hasBody = false)
    Call<JsonObject> testHttp(@Query("start") int start, @Query("count") int count);

    /*使用全路径复写baseUrl，用于非统一baseUrl的场景*/
    @GET
    Call<JsonObject> testUrl(@Url String url);

    /*用于下载文件*/
    @Streaming
    @GET
    Call<ResponseBody> testStreaming(@Url String url);

    /*URL占位符，用于替换和动态更新，相应的参数必须使用相同的字符串被@Path进行注释*/
    @GET("{type}")
    Call<JsonObject> testPath(@Path("type") String type, @Query("start") int start, @Query("count") int count);

    /*@Query，@QueryMap 查询参数，用于GET查询，两者都可以约定是否需要encode，默认false*/
    /*@Query(value = "start", encoded = true) int start*/
    @GET("top250")
    Call<JsonObject> testQueryMap(@QueryMap(encoded = true) Map<String, Object> params);

    /*用于POST、PUT、PATCH请求体，将实例对象根据GsonConverterFactory定义的转化方式转换为对应的json字符串参数*/
    /*@Body与@FormUrlEncoded、@Field不能同时使用*/
    @PUT("update")
    Call<JsonObject> testBody(@Body User user);

    /*@Field，@FieldMap 为form表单形式的键值对，需要添加@FormUrlEncoded表示表单提交 Content-Type:application/x-www-form-urlencoded*/
    @FormUrlEncoded
    @POST("update")
    Call<JsonObject> testField(@Field("name") String name, @Field("age") int age);

    /*@Part，@PartMap 用于POST文件上传，其中@Part MultipartBody.Part代表文件，@Part("key") RequestBody或其它类型代表参数
    需要添加@Multipart表示支持文件上传的表单，Content-Type: multipart/form-data*/
    @Multipart
    @POST("upload")
    Call<JsonObject> testPart(@Part("desc") String desc, @Part MultipartBody.Part file);

    /*@Header，@Headers 不能被互相覆盖*/
    @Headers({
            "token:test override",
            "User-Agent: Wanzi-Retrofit-Sample-App"
    })
    @GET("top250")
    Call<JsonObject> testHeader(@Header("token") String token, @Query("start") int start, @Query("count") int count);
}
