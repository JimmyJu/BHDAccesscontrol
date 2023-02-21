package com.bhd.accesscontrol.api;

import com.hjq.http.config.IRequestApi;

/**
 * pointname 指令名称：IP：端口|DOOR_REMOTE_OPEN，DOOR_GLOBAL_MODE
 * value 门号,门开/门关  如1,1 解释：1号门开门
 */
public class OpenDoorApi implements IRequestApi {
    @Override
    public String getApi() {
        return "SetCtrl_Value";
    }

    private String pointname;
    private String value;

    public OpenDoorApi setPointname(String pointname) {
        this.pointname = pointname;
        return this;
    }

    public OpenDoorApi setValue(String value) {
        this.value = value;
        return this;
    }

}
