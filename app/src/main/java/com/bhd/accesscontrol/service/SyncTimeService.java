package com.bhd.accesscontrol.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import timber.log.Timber;

public class SyncTimeService extends Service {
    Handler handler = new Handler();
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            Timber.e("service_run");

            initConn();
            handler.postDelayed(this, 2000);
        }
    };

    public SyncTimeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationExcezption("Not y
//        et implemented");
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.postDelayed(mRunnable, 2000);//每两秒执行一次runnable.
        return super.onStartCommand(intent, flags, startId);
    }

    private void initConn() {
//        ServiceConfig.GET_IO_DATA_CV,
//        HashMap<String, String> properties = new HashMap<String, String>();
//        WebServiceUtils.callWebService(ServiceConfig.WEB_SERVER_URL,
//                ServiceConfig.NAMESPACE,
//
//                "getSupportProvince",
//                null,
//                new WebServiceUtils.WebServiceCallBack() {
//                    @Override
//                    public void callBack(SoapObject result) {
//                        if (result != null) {
//                            Log.e("TAG", "callBack: " + result );
//                        } else {
//                            ToastUtils.show("服务异常");
//                        }
//                    }
//                });
    }
}