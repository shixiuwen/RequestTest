package shixia.requesttest.http;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import shixia.requesttest.application.MyApplication;


/**
 * Created by ShiXiuwen on 2017/5/9.
 * <p>
 * Email:shixiuwen1991@yeah.net
 * Description:
 */

public class BaseHttpApi {

    private BaseHttpRequest baseRequest;

    //初始化HttpClient
    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .writeTimeout(5000, TimeUnit.MILLISECONDS).build();

    /**
     * 采用POST请求时设置请求体，发送请求之前请务必调用该方法
     *
     * @param request request
     */
    public void setRequest(BaseHttpRequest request) {
        baseRequest = request;
    }

    public void get(String url) {
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        //创建一个Request
        final Request request = new Request.Builder()
                .url(url)
                .build();
        //new call
        Call call = mOkHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(new JsonCallback());
    }

    /**
     * 主要用途：一般请求,测试接口专用
     * <p>
     * 发送携带简单参数的Http POST请求
     */
    public void post(String url) throws IllegalArgumentException{
        FormBody requestBody = getSimpleRequestBody(baseRequest);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new JsonCallback());
    }

    /**
     * 对应一般http请求，得到简单http请求参数表单（POST）
     *
     * @param baseRequest 请求列表
     * @return 参数表单
     */
    private FormBody getSimpleRequestBody(BaseHttpRequest baseRequest) {
        //传输表单
        FormBody.Builder builder = new FormBody.Builder();

        Map<String, String> paramsMap = baseRequest.getParamsMap();
        if (null != paramsMap) {
            for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                builder.add(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }

        return builder.build();
    }

    /**
     * #################################  自定义请求Callback  ###############################
     */

    /**
     * 简单http请求回调,将请求结果转化为对象
     */
    private class JsonCallback implements Callback {

        @Override
        public void onFailure(Call call, final IOException e) {
            if (jsonResponseListener != null) {
                //主线程执行回调操作
                MyApplication.UIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        jsonResponseListener.onFailure(e, "请求失败，请重试！");
                    }
                });
            }
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String result = response.body().string();
            //主线程执行回调操作
            MyApplication.UIHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (jsonResponseListener != null) {
                        jsonResponseListener.onSuccessful(result);
                    }
                }
            });

        }
    }


    /**
     * #################################  网络请求自定义回调接口  ###############################
     * <p>
     * 回调的实现在网络请求处
     */

    private OnJsonHttpResponseListener jsonResponseListener;

    public void setOnJsonHttpResponseListener(OnJsonHttpResponseListener jsonResponseListener) {
        this.jsonResponseListener = jsonResponseListener;
    }

    //网络请求成功或者失败的回调
    public interface OnJsonHttpResponseListener {
        void onFailure(Exception e, String s);

        void onSuccessful(String rawJsonString);
    }

}
