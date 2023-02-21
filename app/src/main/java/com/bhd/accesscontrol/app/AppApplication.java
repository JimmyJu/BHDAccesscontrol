package com.bhd.accesscontrol.app;

import android.app.Application;
import android.content.Context;

import com.bhd.accesscontrol.R;
import com.bhd.accesscontrol.model.RequestHandler;
import com.bhd.accesscontrol.service.RequestServer;
import com.hjq.http.EasyConfig;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestInterceptor;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;
import com.hjq.toast.ToastInterceptor;
import com.hjq.toast.ToastUtils;
import com.hjq.toast.style.ToastBlackStyle;

import okhttp3.OkHttpClient;

public final class AppApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        initSdk(this);

        // 网络请求框架初始化
        IRequestServer server = new RequestServer();
        EasyConfig.with(new OkHttpClient())
                // 是否打印日志
                //.setLogEnabled(BuildConfig.DEBUG)
                // 设置服务器配置
                .setServer(server)
                // 设置请求处理策略
                .setHandler(new RequestHandler(this))
                // 设置请求参数拦截器
                .setInterceptor(new IRequestInterceptor() {
                    @Override
                    public void interceptArguments(IRequestApi api, HttpParams params, HttpHeaders headers) {
                        headers.put("timestamp", String.valueOf(System.currentTimeMillis()));
                    }
                })
                // 设置请求重试次数
                .setRetryCount(1)
                // 设置请求重试时间
                .setRetryTime(2000)
                // 添加全局请求参数
//                .addParam("token", "6666666")
                // 添加全局请求头
                //.addHeader("time", "20191030")
                .into();
    }


    /**
     * 初始化一些第三方框架
     */
    public static void initSdk(Application application) {
        // 初始化吐司
        ToastUtils.init(application, new ToastBlackStyle(application) {

            @Override
            public int getCornerRadius() {
                return (int) application.getResources().getDimension(R.dimen.button_round_size);
            }
        });

        // 设置 Toast 拦截器
        ToastUtils.setToastInterceptor(new ToastInterceptor());

    }


    public static Context getCustomApplicationContext() {
        return mContext;
    }
}
