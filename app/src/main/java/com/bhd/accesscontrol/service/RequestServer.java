package com.bhd.accesscontrol.service;

import com.bhd.accesscontrol.utils.Utils;
import com.hjq.http.config.IRequestServer;

public class RequestServer implements IRequestServer {
    @Override
    public String getHost() {
//        return "http://192.168.1.201:2014/";
        return Utils.getServerUrl();
    }

    @Override
    public String getPath() {
        return "CommRLWebService.asmx/";
    }
}
