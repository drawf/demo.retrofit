package tv.wanzi.demo.retrofit.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tv.wanzi.demo.retrofit.BuildConfig;
import tv.wanzi.demo.retrofit.R;
import tv.wanzi.demo.retrofit.adapter.custom.CustomCallAdapterFactory;
import tv.wanzi.demo.retrofit.api.MovieService;
import tv.wanzi.demo.retrofit.converter.string.StringConverterFactory;
import tv.wanzi.demo.retrofit.databinding.ActivityMainBinding;
import tv.wanzi.demo.retrofit.entity.User;
import tv.wanzi.demo.retrofit.interceptor.LoggingInterceptor;
import tv.wanzi.demo.retrofit.utils.FileUtils;
import tv.wanzi.demo.retrofit.utils.HttpUtils;
import tv.wanzi.demo.retrofit.utils.LogUtils;
import tv.wanzi.demo.retrofit.utils.ToastUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding mBinding;
    private MovieService mMovieService;
    private Call<JsonObject> mCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(LoggingInterceptor.getInstance(LoggingInterceptor.LOG.DEFAULT))
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(MovieService.BASE_URL)
                .addConverterFactory(StringConverterFactory.create())//都支持的类型优先使用第一个
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CustomCallAdapterFactory.create())//自定义请求适配器
                .validateEagerly(BuildConfig.DEBUG)//是否在调用create(Class)时检测接口定义是否正确，而不是在调用方法才检测，适合在开发、测试时使用
                .build();

        mMovieService = retrofit.create(MovieService.class);
        mCall = mMovieService.getTopMovie(0, 2);
        mCall.cancel();//取消请求
        //mCall执行方法只能调用一次,否则会抛IllegalStateException
        mBinding.btnEnqueue.setOnClickListener(this);
        mBinding.btnExecute.setOnClickListener(this);

        mBinding.btnHttp.setOnClickListener(this);
        mBinding.btnUrl.setOnClickListener(this);
        mBinding.btnStreaming.setOnClickListener(this);
        mBinding.btnPath.setOnClickListener(this);
        mBinding.btnQueryMap.setOnClickListener(this);
        mBinding.btnBody.setOnClickListener(this);
        mBinding.btnField.setOnClickListener(this);
        mBinding.btnPart.setOnClickListener(this);
        mBinding.btnHeader.setOnClickListener(this);

        mBinding.btnStringConverter.setOnClickListener(this);
        mBinding.btnCustomCallAdapter.setOnClickListener(this);

        mBinding.btnTestMap.setOnClickListener(this);
        mBinding.btnTestList.setOnClickListener(this);

        mBinding.btnLoggingInterceptor.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_enqueue:
                enqueue();
                break;
            case R.id.btn_execute:
                execute();
                break;
            case R.id.btn_http:
                http();
                break;
            case R.id.btn_url:
                url();
                break;
            case R.id.btn_streaming:
                streaming();
                break;
            case R.id.btn_path:
                path();
                break;
            case R.id.btn_query_map:
                queryMap();
                break;
            case R.id.btn_body:
                body();
                break;
            case R.id.btn_field:
                field();
                break;
            case R.id.btn_part:
                part();
                break;
            case R.id.btn_header:
                header();
                break;
            case R.id.btn_string_converter:
                stringConverter();
                break;
            case R.id.btn_custom_call_adapter:
                customCallAdapter();
                break;
            case R.id.btn_test_map:
                testMap();
                break;
            case R.id.btn_test_list:
                testList();
                break;
            case R.id.btn_logging_interceptor:
                testList();
                break;
        }
        ToastUtils.show("快去看log");
    }


    private void enqueue() {
        mCall.clone().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                LogUtils.i("异步请求：" + response.code());//HTTP status code.
                LogUtils.i("异步请求：" + response.isSuccessful());//Returns true if code() is in the range [200..300).
                LogUtils.i("异步请求：" + response.message());//HTTP status message or null if unknown.
                LogUtils.i("异步请求：" + response.headers());//HTTP headers.

                LogUtils.i("异步请求：" + response.raw());//The raw response from the HTTP client. 打印发现该方法返回数据不全
                LogUtils.i("异步请求：" + response.body());//The deserialized response body of a successful response.它就是你想要的数据
                LogUtils.i("异步请求：" + response.errorBody());//The raw response body of an unsuccessful response.
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                LogUtils.i("failure", t);
            }
        });
    }

    private void execute() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response<JsonObject> response = mCall.clone().execute();
                    LogUtils.i("同步请求：" + response.raw());
                } catch (IOException e) {
                    LogUtils.e(e);
                }
            }
        }).start();
    }

    private void http() {
        Call<JsonObject> call = mMovieService.testHttp(0, 2);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                LogUtils.i("@HTTP注解：" + response.raw());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                LogUtils.i("failure", t);
            }
        });

    }

    private void url() {
        String url = "https://api.douban.com/v2/movie/top250?start=10&count=2";
        Call<JsonObject> call = mMovieService.testUrl(url);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                LogUtils.i("@Url注解：" + response.raw());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                LogUtils.i("failure", t);
            }
        });

    }

    private void streaming() {
        String url = "https://img5.doubanio.com/view/photo/photo/public/p2404720316.jpg";
        Call<ResponseBody> call = mMovieService.testStreaming(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                LogUtils.i("@Streaming注解：" + response.raw());
                InputStream inputStream = response.body().byteStream();
                LogUtils.i("@Streaming注解：" + inputStream);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            byte[] bytes = response.body().bytes();
                            LogUtils.i("@Streaming注解：length=" + bytes.length + "->" + new String(bytes));
                        } catch (IOException e) {
                            LogUtils.e(e);
                        }
                    }
                }).start();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogUtils.i("failure", t);
            }
        });

    }

    private void path() {
        Call<JsonObject> call = mMovieService.testPath("top250", 0, 2);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                LogUtils.i("@Path注解：" + response.raw());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                LogUtils.i("failure", t);
            }
        });
    }

    private void queryMap() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("start", 0);
        params.put("count", 2);

        Call<JsonObject> call = mMovieService.testQueryMap(params);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                LogUtils.i("@QueryMap注解：" + response.raw());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                LogUtils.i("failure", t);
            }
        });
    }

    private void body() {
        User user = new User("老郑", 45);

        Call<JsonObject> call = mMovieService.testBody(user);
        try {
            LogUtils.i("@Body注解：" + HttpUtils.requestBody2String(call.request().body()));
        } catch (IOException e) {
            LogUtils.e(e);
        }
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                LogUtils.i("@Body注解：" + response.raw());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                LogUtils.i("failure", t);
            }
        });
    }

    private void field() {
        Call<JsonObject> call = mMovieService.testField("老王", 45);
        try {
            LogUtils.i("@Field注解：" + HttpUtils.requestBody2String(call.request().body()));
        } catch (IOException e) {
            LogUtils.e(e);
        }
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                LogUtils.i("@Field注解：" + response.raw());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                LogUtils.i("failure", t);
            }
        });
    }

    private void part() {
        File file = FileUtils.getEFDFile("image/abc.jpg");
        if (null == file) return;

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part image = MultipartBody.Part.createFormData("image", "image name", requestBody);

        Call<JsonObject> call = mMovieService.testPart("文件描述", image);
        try {
            LogUtils.i("@Part注解：" + HttpUtils.requestBody2String(call.request().body()));
        } catch (IOException e) {
            LogUtils.e(e);
        }
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                LogUtils.i("@Part注解：" + response.raw());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                LogUtils.i("failure", t);
            }
        });
    }

    private void header() {
        Call<JsonObject> call = mMovieService.testHeader("i am token", 0, 2);
        LogUtils.i("@Header注解：" + call.request().headers());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                LogUtils.i("@Header注解：" + response.raw());
                LogUtils.i("@Header注解：" + response.headers());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                LogUtils.i("failure", t);
            }
        });
    }

    private void stringConverter() {
        Call<String> call = mMovieService.testStringConverter(0, 2);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                LogUtils.i("StringResponseBodyConverter：" + response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                LogUtils.i("failure", t);
            }
        });
    }

    private void customCallAdapter() {
        final CustomCallAdapterFactory.CustomCall<String> customCall = mMovieService.testCustomCallAdapter(0, 2);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = customCall.getResult();
                    LogUtils.i("CustomCallAdapter：" + result);
                } catch (IOException e) {
                    LogUtils.e(e);
                }
            }
        }).start();
    }

    private void testMap() {
        Call<Map<String, Object>> call = mMovieService.testMap(0, 2);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                Map<String, Object> body = response.body();
                LogUtils.i("Test Map：" + body);
                LogUtils.i("Test Map：" + body.get("title"));
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                LogUtils.i("failure", t);
            }
        });
    }

    private void testList() {
        Call<List<Map<String, Object>>> call = mMovieService.testList("https://movie.douban.com/j/cinemas/?city_id=108288&limit=5", 0, 2);
        call.enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                List<Map<String, Object>> body = response.body();
                LogUtils.i("Test List：" + body);
                LogUtils.i("Test List：" + body.get(0));
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                LogUtils.i("failure", t);
            }
        });
    }


}
