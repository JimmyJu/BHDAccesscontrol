package com.bhd.accesscontrol.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bhd.accesscontrol.R;
import com.bhd.accesscontrol.adapter.GridAdapter;
import com.bhd.accesscontrol.aop.SingleClick;
import com.bhd.accesscontrol.api.OpenDoorApi;
import com.bhd.accesscontrol.bean.GroupName;
import com.bhd.accesscontrol.bean.InfoBean;
import com.bhd.accesscontrol.service.SyncTimeService;
import com.bhd.accesscontrol.utils.DensityUtil;
import com.bhd.accesscontrol.utils.DoubleClickHelper;
import com.bhd.accesscontrol.utils.SPUtils;
import com.bhd.accesscontrol.utils.Utils;
import com.gavin.com.library.StickyDecoration;
import com.gavin.com.library.listener.GroupListener;
import com.gavin.com.library.listener.OnGroupClickListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hjq.base.BaseDialog;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.toast.ToastUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;

import static com.hjq.http.EasyUtils.postDelayed;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private ImageView mImageView, iv_refresh;
    private GridAdapter mGridAdapter;
    private List<InfoBean> mBeanList;
    private List<GroupName> mGroupNames;
    //    private LoadingProgressDialog dialog;
    private List<String> mIps;

    /**
     * ???????????????
     */
    private BaseDialog mWaitDialog;

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initView();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//        initConn();
//        handler.postDelayed(() -> mGridAdapter.notifyDataSetChanged(), 1000);

//            }
//        }, 1000);
//        initConn();

        mGridAdapter = new GridAdapter(mBeanList);

        //------------- StickyDecoration ????????????  ----------------
//        StickyDecoration.Builder builder = StickyDecoration.Builder.init(new GroupListener() {
        StickyDecoration decoration = StickyDecoration.Builder.init(new GroupListener() {
                    @Override
                    public String getGroupName(int position) {
                        //????????????
                        if (mBeanList.size() > position && position > -1) {
                            //?????????????????????????????????????????????
                            return mBeanList.get(position).getGroupname() + mBeanList.get(position).getDoorName().substring(0, 10);
                            //+ mBeanList.get(position).getDoorName().substring(0, 10)
                        }
                        return null;
                    }
                })
                //?????????
                .setGroupBackground(Color.parseColor("#48BDFF"))
                //??????
                .setGroupHeight(DensityUtil.dip2px(this, 40))
                //???????????????
                .setDivideColor(Color.parseColor("#EE96BC"))
                //??????????????? (?????????????????????)
                .setDivideHeight(DensityUtil.dip2px(this, 2))
                //???????????? ????????????
                .setGroupTextColor(Color.BLACK)
                //????????????
                .setGroupTextSize(DensityUtil.sp2px(this, 15))
                // ??????   ?????????????????????  ?????????????????????
                .setTextSideMargin(DensityUtil.dip2px(this, 10))
                //Group????????????
                .setOnClickListener(new OnGroupClickListener() {
                    @Override
                    public void onClick(int position, int id) {
                        //????????????????????????????????????????????????item???position
//                        String content = "onGroupClick --> " + position + " " +  mBeanList.get(position).getGroupname() + "__"+mBeanList.get(position).getIp()+"__"+ mBeanList.get(position).getDoor();
//                        Toast.makeText(MainActivity.this, content, Toast.LENGTH_SHORT).show();
                        new SelectDialog.Builder(mContext)
                                .setTitle("?????????????????????")
                                //??????????????????
                                .setList(mBeanList.get(position).getDoorName().substring(0, 10))
                                .setSingleSelect()
                                .setSelect(0)
                                .setCancelable(false)
                                .setListener(new SelectDialog.OnListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    @Override
                                    public void onSelected(BaseDialog dialog, HashMap data) {
                                        // ?????????????????????
                                        if (mIps != null) {
                                            mIps.clear();
                                        }
//                                        String currentIp = mBeanList.get(position).getIp();
//                                        String[] doors = new String[mBeanList.size()];
//                                        for (int i = 0; i < mBeanList.size(); i++) {
//                                            if (currentIp.equals(mBeanList.get(i).getIp())) {
//                                                //????????????
//                                                String door = mBeanList.get(i).getDoor();
//                                                doors[i] = door.substring(door.length() - 1);
//                                            }
//                                        }
//                                        Log.i("Tag", "??????IP" + currentIp + "??????" + Arrays.toString(doors));
//                                        batchOperation(currentIp, doors);

                                        //????????????
                                        String currentGroupName = mBeanList.get(position).getGroupname();
                                        for (int i = 0; i < mBeanList.size(); i++) {
                                            //?????????????????????mIps?????????
                                            if (currentGroupName.equals(mBeanList.get(i).getGroupname())) {
                                                mIps.add(mBeanList.get(i).getIp() + "-" + mBeanList.get(i).getDoor());
                                            }
                                        }
//                                        Log.e("TAG", "ips: " + mIps.toString());
                                        batchOperation(mIps);
                                    }
                                })
                                .show();
                    }
                })
                .build();

//        StickyDecoration decoration = builder.build();


//        handler.postDelayed(() ->  mRecyclerView.addItemDecoration(new StickHeaderDecoration(this)), 100);
        //???????????????????????????????????????????????????
//        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 9));

        RecyclerView.LayoutManager manager;
//        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);//????????????
        manager = new GridLayoutManager(this, 6);//????????????
        //??????span?????????GridLayoutManager??????????????????
        decoration.resetSpan(mRecyclerView, (GridLayoutManager) manager);//?????????????????????????????????

        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(decoration);


        mRecyclerView.setAdapter(mGridAdapter);

        mGridAdapter.setOnItemClickListener(new GridAdapter.OnItemClickListener() {
            @SingleClick
            @Override
            public void onItemClick(View view, String ipPort, String door, String doorInfo) {
//                Log.e("TAG", "onItemClick: " + ipPort);
                new SelectDialog.Builder(mContext)
                        .setTitle("???????????????")
//                        .setList(ipPort.substring(0, ipPort.indexOf(":")) + " " + door)
                        .setList(doorInfo)
                        // ??????????????????
                        .setSingleSelect()
                        // ??????????????????
                        .setSelect(0)
                        //?????????????????????????????????????????????dialog?????????
                        .setCancelable(false)
                        .setListener(new SelectDialog.OnListener<String>() {

                            @Override
                            public void onSelected(BaseDialog dialog, HashMap<Integer, String> data) {
//                                Collection<String> values = data.values();
//                                String str = values.toString();
//                                String doorNumber = str.substring(str.length() - 2, str.length() - 1);
//
//                                String command = ipPort + "|DOOR_REMOTE_OPEN";
//                                SetCtrl_Value(command, doorNumber + ",1");
////                                ToastUtils.show("?????????" + substring + ",1");
//                                Log.e("TAG", "command: " + command + ": " + doorNumber + ",1");

                                String doorNumber = door.substring(door.length() - 1);
//

                                showOperate(ipPort, doorNumber);

                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {
//                                ToastUtils.show("??????");
                            }
                        })
                        .show();
            }
        });

//        openService();

    }


    private void initView() {
        //????????????????????????
//        dialog = new LoadingProgressDialog(MainActivity.this, "", R.drawable.frame);
//        dialog.setCanceledOnTouchOutside(false);

        mRecyclerView = findViewById(R.id.recyclerview);
        mBeanList = new ArrayList<>();
        mGroupNames = new ArrayList<>();
        mIps = new ArrayList<>();
        iv_refresh = findViewById(R.id.refresh);
        mImageView = findViewById(R.id.ip_set);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomizeDialog();
            }
        });

        iv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HintDialog.Builder(mContext).setIcon(HintDialog.ICON_FINISH).setMessage("???????????????????????????").show();
                mGroupNames.clear();
                mGridAdapter.clear();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initConn();
                        handler.postDelayed(() -> mGridAdapter.notifyDataSetChanged(),
                                1000);
                    }
                }, 1000);
            }
        });

    }

    private void openService() {
        if (!Utils.isServiceRunning(mContext, "com.bhd.accesscontrol.service.SyncTimeService")) {
            Intent intent = new Intent(MainActivity.this, SyncTimeService.class);
            startService(intent);
        }
    }

    /**
     * ????????????
     */
    private void initConn() {

//        EasyHttp.post(this)
//                .api("GetIOData_CV")
//                .request(new OnHttpListener<Object>() {
//                    @Override
//                    public void onSucceed(Object result) {
//                        Log.e("TAG", "onSucceed: " + result.toString());
//                        if (result != null) {
//                            Gson gson = new Gson();
//                            String resultData = gson.toJson(result);
                           /* String resultData1 = "{\"flag\":\"true\",\"Info\":{" +
                                    "\"172.17.19.80:2000|DoorGrpup\":\"1\"," +
                                    "\"172.17.19.80:2000|DOOR_REMOTE_OPEN\":\"02,01^SO\"," +
                                    "\"172.17.19.80:2000|DOOR_GLOBAL_MODE\":\"02,00^SO\"," +
                                    "\"172.17.19.80:2000|DOOR3_MAG\":\"1^DI\"," +
                                    "\"172.17.19.80:2000|DOOR2_MAG\":\"1^DI-1??????\"," +
                                    "\"172.17.19.80:2000|DOOR1_MAG\":\"1^DI-1??????\"," +
                                    "\"172.17.19.80:2000|DOOR4_MAG\":\"1^DI\"," +
                                    "\"172.17.19.81:2000|DoorGrpup\":\"2\"," +
                                    "\"172.17.19.81:2000|DOOR_REMOTE_OPEN\":\"02,01^SO\"," +
                                    "\"172.17.19.81:2000|DOOR_GLOBAL_MODE\":\"02,00^SO\"," +
                                    "\"172.17.19.81:2000|DOOR3_MAG\":\"1^DI\"," +
                                    "\"172.17.19.81:2000|DOOR2_MAG\":\"1^DI-2??????\"," +
                                    "\"172.17.19.81:2000|DOOR1_MAG\":\"1^DI-1??????\"," +
                                    "\"172.17.19.81:2000|DOOR4_MAG\":\"1^DI\"," +
                                    "\"172.17.19.82:2000|DoorGrpup\":\"1\"," +
                                    "\"172.17.19.82:2000|DOOR_REMOTE_OPEN\":\"02,01^SO\"," +
                                    "\"172.17.19.82:2000|DOOR_GLOBAL_MODE\":\"02,00^SO\"," +
                                    "\"172.17.19.82:2000|DOOR3_MAG\":\"1^DI\"," +
                                    "\"172.17.19.82:2000|DOOR2_MAG\":\"1^DI-2??????\"," +
                                    "\"172.17.19.82:2000|DOOR1_MAG\":\"1^DI-1??????\"," +
                                    "\"172.17.19.82:2000|DOOR4_MAG\":\"1^DI\"," +
                                    "\"172.17.19.83:2000|DoorGrpup\":\"4\"," +
                                    "\"172.17.19.83:2000|DOOR_REMOTE_OPEN\":\"02,01^SO\"," +
                                    "\"172.17.19.83:2000|DOOR_GLOBAL_MODE\":\"02,00^SO\"," +
                                    "\"172.17.19.83:2000|DOOR3_MAG\":\"1^DI\"," +
                                    "\"172.17.19.83:2000|DOOR2_MAG\":\"1^DI-2??????\"," +
                                    "\"172.17.19.83:2000|DOOR1_MAG\":\"1^DI-1??????\"," +
                                    "\"172.17.19.83:2000|DOOR4_MAG\":\"1^DI\"," +
                                    "\"172.17.19.84:2000|DoorGrpup\":\"5\"," +
                                    "\"172.17.19.84:2000|DOOR_REMOTE_OPEN\":\"02,01^SO\"," +
                                    "\"172.17.19.84:2000|DOOR_GLOBAL_MODE\":\"02,00^SO\"," +
                                    "\"172.17.19.84:2000|DOOR3_MAG\":\"1^DI\"," +
                                    "\"172.17.19.84:2000|DOOR2_MAG\":\"1^DI-2??????\"," +
                                    "\"172.17.19.84:2000|DOOR1_MAG\":\"1^DI-1??????\"," +
                                    "\"172.17.19.84:2000|DOOR4_MAG\":\"1^DI\"},\"Error\":\"\"}";*/


//        Log.e("TAG", "11: " + resultData2.toString());
        String jsonData = Utils.getJson(this, "access_format.json");
        JsonObject json = (JsonObject) JsonParser.parseString(jsonData);

//        JsonParser parse = new JsonParser();
//        JsonObject json = (JsonObject) parse.parse(resultData2);
        if ("true".equals(json.get("flag").getAsString())) {
            String s = json.get("Info").getAsJsonObject().toString();
            String s1 = s.substring(1, s.length() - 1);
            String[] arr = s1.split(",");
            for (int i = 0; i < arr.length; i++) {
//                                    if (arr[i].contains("MAG")) {
//                                        String door = arr[i].substring(arr[i].indexOf("|") + 1, arr[i].indexOf("_"));
//                                        String ipPort = arr[i].substring(1, arr[i].indexOf("|"));
//                                        String doorInfo = arr[i].substring(arr[i].indexOf("-") + 1).replace("\"", "");
//                                        if (!"DOOR3".equals(door) && !"DOOR4".equals(door)) {
//                                            mBeanList.add(new InfoBean(ipPort, door, doorInfo));
//                                            Log.e("TAG", "initConn: " + ipPort + "__?????????__" + doorInfo);
//                                        }
//                                    }


                if (arr[i].contains("DoorGrpup")) {
                    String groupIp = arr[i].substring(1, arr[i].indexOf("|"));
                    String doorGroup = arr[i].substring(arr[i].indexOf("|") + 1);
                    String groupName = doorGroup.substring(doorGroup.indexOf(":") + 1).replace("\"", "");
                    mGroupNames.add(new GroupName(groupIp, groupName));
                    Log.e("TAG", "mGroupNames size: " + mGroupNames.size());
//                                        Log.e("TAG", "GroupName: " + mGroupNames.toString());
                }

//                                    for (int j = 0; j < mGroupNames.size(); j++) {
//                                        String groupIp = mGroupNames.get(j).getGroupIp();
//                                        String groupNames = mGroupNames.get(j).getGroupNames();
                                    /*if (arr[i].contains("MAG")) {
                                        String door = arr[i].substring(arr[i].indexOf("|") + 1, arr[i].indexOf("_"));
                                        String ipPort = arr[i].substring(1, arr[i].indexOf("|"));
                                        String doorInfo = arr[i].substring(arr[i].indexOf("-") + 1).replace("\"", "");
                                        if (!"DOOR3".equals(door) && !"DOOR4".equals(door)) {
                                            for (int j = 0; j < mGroupNames.size(); j++) {
                                                String groupIp = mGroupNames.get(j).getGroupIp();
                                                String groupNames = mGroupNames.get(j).getGroupNames();
                                                if (groupIp.equals(ipPort)) {
                                                    mBeanList.add(new InfoBean(ipPort, door, doorInfo, groupNames));
                                                    Log.e("TAG", "mlist: " + ipPort + "__?????????__" + doorInfo + "__???__" + groupNames);
                                                }
                                            }
                                        }
                                    }*/
//                                    }
            }
            //???????????? ???IP????????????
            for (int j = 0; j < mGroupNames.size(); j++) {
                for (int i = 0; i < arr.length; i++) {
                    if (arr[i].contains("MAG")) {
                        String door = arr[i].substring(arr[i].indexOf("|") + 1, arr[i].indexOf("_"));
                        String ipPort = arr[i].substring(1, arr[i].indexOf("|"));
                        String doorInfo = arr[i].substring(arr[i].indexOf("-") + 1).replace("\"", "");
                        if (!"DOOR2".equals(door) && !"DOOR3".equals(door) && !"DOOR4".equals(door)) {
                            String groupIp = mGroupNames.get(j).getGroupIp();
                            String groupNames = mGroupNames.get(j).getGroupNames();
                            if (groupIp.equals(ipPort)) {
                                mBeanList.add(new InfoBean(ipPort, door, doorInfo, groupNames));
                            }
                        }
                    }
                }

            }

        } else if ("false".equals(json.get("flag").getAsString())) {
//                                ToastUtils.show(json.get("Error").getAsString());
            new HintDialog.Builder(mContext).setIcon(HintDialog.ICON_WARNING)
                    .setMessage(json.get("Error").getAsString()).show();
        }


//                        } else {
//                            new HintDialog.Builder(mContext).setIcon(HintDialog.ICON_ERROR).setMessage("????????????").show();
//                        }
//                    }
//
//                    @Override
//                    public void onFail(Exception e) {
//                        Log.e("TAG", "onFail: " + e.toString());
//                        new HintDialog.Builder(mContext).setIcon(HintDialog.ICON_ERROR).setMessage("???????????????????????????????????????").show();
//                    }
//                });
        Collections.sort(mBeanList);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mContext != null) {
            mContext = null;
        }
    }

    /**
     * ?????????IP?????????|??????
     * 192.168.3.38:2000|DOOR_REMOTE_OPEN //????????????
     * 192.168.3.38:2000|DOOR_GLOBAL_MODE //???????????????
     * <p>
     * ???????????????
     * 00?????????
     * 01?????????
     * <p>
     * ??????????????????
     * 00?????????
     * 01?????????????????????
     * 02????????????????????????
     * ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param pointname ???????????????IP?????????|DOOR_REMOTE_OPEN
     * @param value     ??????,??????/??????  ???1,1 ?????????1????????????
     */
    private void SetCtrl_Value(String pointname, String value) {

        EasyHttp.post(this)
                .api(new OpenDoorApi()
                        .setPointname(pointname)
                        .setValue(value))
                .request(new OnHttpListener<Object>() {
                    @Override
                    public void onSucceed(Object result) {
                        if (result != null) {
                            Gson gson = new Gson();
                            String resultData = gson.toJson(result);
                            JsonParser parse = new JsonParser();
                            JsonObject json = (JsonObject) parse.parse(resultData);
                            if ("true".equals(json.get("flag").getAsString())) {
//                                String s = json.get("Info").getAsJsonObject().toString();
//                                String s1 = s.substring(1, s.length() - 1).replace("\"", "");

                                new HintDialog.Builder(mContext).setIcon(HintDialog.ICON_FINISH).setMessage("????????????").show();
                            } else if ("false".equals(json.get("flag").getAsString())) {
                                new HintDialog.Builder(mContext).setIcon(HintDialog.ICON_WARNING)
                                        .setMessage(json.get("Error").getAsString()).show();
                            }

                        } else {
                            new HintDialog.Builder(mContext).setIcon(HintDialog.ICON_ERROR).setMessage("????????????").show();
                        }

                    }

                    @Override
                    public void onFail(Exception e) {
                        new HintDialog.Builder(mContext).setIcon(HintDialog.ICON_ERROR).setMessage("??????????????????").show();
                    }

                    @Override
                    public void onStart(Call call) {
//                        dialog.show();
//                        Log.e("TAG", "onStart: " + "????????????");

                        if (mWaitDialog == null) {
                            mWaitDialog = new WaitDialog.Builder(mContext)
                                    // ??????????????????????????????
                                    .setMessage(getString(R.string.common_loading))
                                    .create();
                        }
                        if (!mWaitDialog.isShowing()) {
                            mWaitDialog.show();
//                            postDelayed(mWaitDialog::dismiss, 2000);
                        }

                    }

                    @Override
                    public void onEnd(Call call) {
//                        dialog.dismiss();
//                        Log.e("TAG", "onStart: " + "????????????");
                        mWaitDialog.dismiss();
                    }
                });

//        HashMap<String, String> properties = new HashMap<String, String>();
//        properties.put("pointname", pointname);
//        properties.put("value", value);
//        WebServiceUtils.callWebService(ServiceConfig.WEB_SERVER_URL,
//                ServiceConfig.NAMESPACE,
//                ServiceConfig.SET_CTRL_VALUE,
//                properties,
//                new WebServiceUtils.WebServiceCallBack() {
//                    @Override
//                    public void callBack(SoapObject result) {
//                        if (result != null) {\
//                            String resultData = result.getProperty(0).toString();
//                            JsonParser parse = new JsonParser();
//                            JsonObject json = (JsonObject) parse.parse(resultData);
//                            if ("true".equals(json.get("flag").getAsString())) {
//                                String s = json.get("Info").getAsJsonObject().toString();
//                                String s1 = s.substring(1, s.length() - 1).replace("\"", "");
//                                String s2 = s1.substring(s1.indexOf("|") + 1);
//                                String state = s2.substring(s2.indexOf(":") + 1);
//                                Log.e("TAG", "SetCtrl_Value: " + state);
//                                new HintDialog.Builder(mContext).setIcon(HintDialog.ICON_FINISH).setMessage("????????????").show();
//
//                            } else if ("false".equals(json.get("flag").getAsString())) {
////                                ToastUtils.show(json.get("Error").getAsString());
//                                new HintDialog.Builder(mContext).setIcon(HintDialog.ICON_WARNING).setMessage(json.get("Error").getAsString()).show();
//                            }
//                        } else {
//                            new HintDialog.Builder(mContext).setIcon(HintDialog.ICON_ERROR).setMessage("????????????").show();
//                        }
//                    }
//                }
//        );

    }

    /**
     * ???????????????IP
     */
    private void showCustomizeDialog() {
        /* @setView ???????????????View ==> R.layout.dialog_customize
         * dialog_customize.xml????????????????????????View
         */

        String ip = (String) SPUtils.get(mContext, "IP", "");
        String port = (String) SPUtils.get(mContext, "PORT", "");
        String serverUrl = "";
        if (!ip.isEmpty() && !port.isEmpty()) {
            serverUrl = "??????????????????" + ip + ":" + port;
        }


        AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(mContext);
        final View dialogView = LayoutInflater.from(mContext)
                .inflate(R.layout.dialog_customize, null);
        customizeDialog.setTitle(serverUrl);
        customizeDialog.setView(dialogView);
        customizeDialog.setPositiveButton("??????",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        TextView tv_show = dialogView.findViewById(R.id.tv_ipshow);
                        // ??????EditView??????????????????
                        EditText edit_ip =
                                (EditText) dialogView.findViewById(R.id.ed_ip);
                        EditText edit_port =
                                (EditText) dialogView.findViewById(R.id.ed_port);
                        String ed_ip = edit_ip.getText().toString();
                        String ed_port = edit_port.getText().toString();
                        if (!ed_ip.isEmpty() && !ed_port.isEmpty()) {
                            SPUtils.put(mContext, "IP", ed_ip);
                            SPUtils.put(mContext, "PORT", ed_port);
                            Log.e("TAG", "onClick: " + edit_ip.getText().toString() + ":" + edit_port.getText().toString());
                            String ip = (String) SPUtils.get(mContext, "IP", "");
                            String port = (String) SPUtils.get(mContext, "PORT", "");
                            if (!ip.isEmpty() && !port.isEmpty()) {
                                new HintDialog.Builder(mContext).setIcon(HintDialog.ICON_FINISH).setMessage("????????????").show();
                                mGridAdapter.clear();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        initConn();
                                        handler.postDelayed(() -> mGridAdapter.notifyDataSetChanged(), 1000);

                                    }
                                }, 1000);
                            }
                        }

                    }
                });
        customizeDialog.setCancelable(false);
        customizeDialog.show();
    }

    private TextView door_state;

    /**
     * ???????????????
     *
     * @param ip
     * @param door
     */
    private void showOperate(String ip, String door) {
        AlertDialog.Builder operateDialog = new AlertDialog.Builder(mContext);
        final View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_door, null);
        operateDialog.setTitle("??????");
        operateDialog.setView(dialogView);

        Button open, close, automatic, normally_open, normally_closed;
//        TextView door_state;
        open = dialogView.findViewById(R.id.open);
        close = dialogView.findViewById(R.id.close);
        automatic = dialogView.findViewById(R.id.automatic);
        normally_open = dialogView.findViewById(R.id.normally_open);
        normally_closed = dialogView.findViewById(R.id.normally_closed);
        door_state = dialogView.findViewById(R.id.door_state);
        doorState(ip, door);
        open.setOnClickListener(view -> {
            String command = ip + "|DOOR_REMOTE_OPEN";
            SetCtrl_Value(command, door + ",1");
            doorState(ip, door);

        });
        close.setOnClickListener(view -> {
            String command = ip + "|DOOR_REMOTE_OPEN";
            SetCtrl_Value(command, door + ",0");
            doorState(ip, door);
        });
        automatic.setOnClickListener(view -> {
            String command = ip + "|DOOR_GLOBAL_MODE";
            SetCtrl_Value(command, door + ",0");
            doorState(ip, door);
        });
        normally_open.setOnClickListener(view -> {
            String command = ip + "|DOOR_GLOBAL_MODE";
            SetCtrl_Value(command, door + ",1");
            doorState(ip, door);

        });
        normally_closed.setOnClickListener(view -> {
            String command = ip + "|DOOR_GLOBAL_MODE";
            SetCtrl_Value(command, door + ",2");
            doorState(ip, door);
        });

        operateDialog.setNegativeButton("??????", (dialogInterface, i) -> dialogInterface.dismiss());
        operateDialog.setCancelable(false);
        operateDialog.show();
    }

    /**
     * ????????????
     */
    private void batchOperation(List<String> ips) {
        AlertDialog.Builder operateDialog = new AlertDialog.Builder(mContext);
        final View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_door, null);
        operateDialog.setTitle("????????????");
        operateDialog.setView(dialogView);

        Button open, close, automatic, normally_open, normally_closed;
//        TextView door_state;
        open = dialogView.findViewById(R.id.open);
        close = dialogView.findViewById(R.id.close);
        automatic = dialogView.findViewById(R.id.automatic);
        normally_open = dialogView.findViewById(R.id.normally_open);
        normally_closed = dialogView.findViewById(R.id.normally_closed);
        door_state = dialogView.findViewById(R.id.door_state);
        open.setOnClickListener(view -> {
//            String command = currentIp + "|DOOR_REMOTE_OPEN";
//            for (String door : doors) {
//                if (door != null) {
//                    SetCtrl_Value(command, door + ",1");
//                    Log.e("TAG", "????????????: " + command + door + ",1");
//                }
//            }

            for (int i = 0; i < ips.size(); i++) {
                String currentIps = ips.get(i);
                String currentIp = currentIps.substring(0, currentIps.indexOf("-"));
                String door = currentIps.substring(currentIps.length() - 1);

                String command = currentIp + "|DOOR_REMOTE_OPEN";
                SetCtrl_Value(command, door + ",1");
                Log.e("TAG", "??????IP??????Open: " + currentIp + "," + door);
            }
        });
        close.setOnClickListener(view -> {
//            String command = currentIp + "|DOOR_REMOTE_OPEN";
//            for (int i = 0; i < doors.length; i++) {
//                String door = doors[i];
//                if (door != null) {
//                    SetCtrl_Value(command, door + ",0");
//                    Log.e("TAG", "????????????: " + command + door + ",0");
//                }
//            }

            for (int i = 0; i < ips.size(); i++) {
                String currentIps = ips.get(i);
                String currentIp = currentIps.substring(0, currentIps.indexOf("-"));
                String door = currentIps.substring(currentIps.length() - 1);

                String command = currentIp + "|DOOR_REMOTE_OPEN";
                SetCtrl_Value(command, door + ",0");
                Log.e("TAG", "??????IP??????close: " + currentIp + "," + door);
            }
        });
        automatic.setOnClickListener(view -> {
//            String command = currentIp + "|DOOR_GLOBAL_MODE";
//            for (int i = 0; i < doors.length; i++) {
//                String door = doors[i];
//                if (door != null) {
//                    SetCtrl_Value(command, door + ",0");
//                    Log.e("TAG", "????????????: " + command + door + ",0");
//                }
//            }

            for (int i = 0; i < ips.size(); i++) {
                String currentIps = ips.get(i);
                String currentIp = currentIps.substring(0, currentIps.indexOf("-"));
                String door = currentIps.substring(currentIps.length() - 1);

                String command = currentIp + "|DOOR_GLOBAL_MODE";
                SetCtrl_Value(command, door + ",0");
                Log.e("TAG", "??????IP??????automatice: " + currentIp + "," + door);
            }

        });
        normally_open.setOnClickListener(view -> {
//            String command = currentIp + "|DOOR_GLOBAL_MODE";
//            for (int i = 0; i < doors.length; i++) {
//                String door = doors[i];
//                if (door != null) {
//                    SetCtrl_Value(command, door + ",1");
//                    Log.e("TAG", "????????????: " + command + door + ",1");
//                }
//            }

            for (int i = 0; i < ips.size(); i++) {
                String currentIps = ips.get(i);
                String currentIp = currentIps.substring(0, currentIps.indexOf("-"));
                String door = currentIps.substring(currentIps.length() - 1);

                String command = currentIp + "|DOOR_GLOBAL_MODE";
                SetCtrl_Value(command, door + ",1");
                Log.e("TAG", "??????IP??????normally_open: " + currentIp + "," + door);
            }

        });
        normally_closed.setOnClickListener(view -> {
//            String command = currentIp + "|DOOR_GLOBAL_MODE";
//            for (int i = 0; i < doors.length; i++) {
//                String door = doors[i];
//                if (door != null) {
//                    SetCtrl_Value(command, door + ",2");
//                    Log.e("TAG", "????????????: " + command + door + ",2");
//                }
//            }

            for (int i = 0; i < ips.size(); i++) {
                String currentIps = ips.get(i);
                String currentIp = currentIps.substring(0, currentIps.indexOf("-"));
                String door = currentIps.substring(currentIps.length() - 1);

                String command = currentIp + "|DOOR_GLOBAL_MODE";
                SetCtrl_Value(command, door + ",2");
                Log.e("TAG", "??????IP??????normally_closed: " + currentIp + "," + door);
            }

        });

        operateDialog.setNegativeButton("??????", (dialogInterface, i) -> dialogInterface.dismiss());
        operateDialog.setCancelable(false);
        operateDialog.show();

    }

    /**
     * ???????????????
     *
     * @param ip   ??????IP??????
     * @param door ??????
     */
    private void doorState(String ip, String door) {
        EasyHttp.post(this)
                .api("GetIOData_CV")
                .request(new OnHttpListener<Object>() {
                    @Override
                    public void onSucceed(Object result) {
                        if (result != null) {
                            Gson gson = new Gson();
                            String resultData = gson.toJson(result);
                            JsonParser parse = new JsonParser();
                            JsonObject json = (JsonObject) parse.parse(resultData);
                            if ("true".equals(json.get("flag").getAsString())) {
                                String s = json.get("Info").getAsJsonObject().toString();
                                String s1 = s.substring(1, s.length() - 1);
                                String[] arr = s1.split(",");
                                for (int i = 0; i < arr.length; i++) {
                                    if (arr[i].contains(ip + "|DOOR" + door)) {
                                        String door = arr[i].substring(arr[i].indexOf("|") + 1).replace("\"", "");
                                        String info = door.substring(door.indexOf(":") + 1, 14);
                                        Log.e("TAG", "onSucceed: " + ip + " " + door + "___" + info);
                                        if ("1^DI".equals(info)) {
                                            door_state.setText("??????????????? ???");
                                        } else if ("0^DI".equals(info)) {
                                            door_state.setText("??????????????? ???");
                                        }
                                    }
//                                    if (arr[i].contains(ip+ "|DOOR_GLOBAL_MODE")) {
//                                        String door = arr[i].substring(arr[i].indexOf("|") + 1).replace("\"", "");
////                                        String info = door.substring(door.indexOf(":") + 1,24);
//                                        Log.e("TAG", "onSucceed: " + door);
//                                    }

                                }
                            } else if ("false".equals(json.get("flag").getAsString())) {
                                new HintDialog.Builder(mContext).setIcon(HintDialog.ICON_WARNING)
                                        .setMessage(json.get("Error").getAsString()).show();
                            }

                        } else {
                            new HintDialog.Builder(mContext).setIcon(HintDialog.ICON_ERROR).setMessage("????????????").show();
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        new HintDialog.Builder(mContext).setIcon(HintDialog.ICON_ERROR).setMessage("??????????????????").show();
                    }
                });
    }


    @Override
    public void onBackPressed() {
        if (!DoubleClickHelper.isOnDoubleClick()) {
            ToastUtils.show(R.string.home_exit_hint);
            return;
        }

        // ?????????????????????????????????????????????
        //            ActivityManager.getInstance().finishAllActivities();
        // ????????????????????????????????? API ?????????????????? Activity onDestroy ???????????????????????????
        //             System.exit(0);
        postDelayed(this::finish, 300);
    }
}