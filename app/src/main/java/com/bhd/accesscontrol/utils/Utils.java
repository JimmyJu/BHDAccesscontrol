package com.bhd.accesscontrol.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import com.bhd.accesscontrol.app.AppApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    /**
     * 判断服务是否开启
     *
     * @param context
     * @param ServiceName
     * @return
     */
    public static boolean isServiceRunning(Context context, String ServiceName) {
        if (TextUtils.isEmpty(ServiceName)) {
            return false;
        }
        ActivityManager myManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService =
                (ArrayList<ActivityManager.RunningServiceInfo>)
                        myManager.getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(ServiceName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Json返回信息
     */
    public static Map<String, String> jsonForMap(String json, String[] param) {
        Map<String, String> map = new HashMap<>();
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(json);
//            map.put("code", jsonObj.getString("code"));
//            map.put("msg", jsonObj.getString("msg"));
//            map.put("user", jsonObj.getString("user"));
            for (String i : param) {
                map.put(i, jsonObj.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 获取服务地址
     */
    public static String getServerUrl() {
        String ed_ip = (String) SPUtils.get(AppApplication.getCustomApplicationContext(), "IP", "192.168.1.201");
        String ed_port = (String) SPUtils.get(AppApplication.getCustomApplicationContext(), "PORT", "2014");
        if (!ed_ip.isEmpty() && !ed_port.isEmpty()) {
//            http://192.168.1.201:2014/
            String host = "http://" + ed_ip + ":" + ed_port + "/";
            Log.e("TAG", "getServerUrl: " + host);
            return host;
        }
        return null;
    }


    /**
     * json文件中的内容
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        AssetManager assetManager = context.getAssets();//asser资源管理器

        try {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(assetManager.open(fileName), StandardCharsets.UTF_8));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }



}