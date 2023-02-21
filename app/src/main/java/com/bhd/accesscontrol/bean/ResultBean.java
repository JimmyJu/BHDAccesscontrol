package com.bhd.accesscontrol.bean;

public class ResultBean {
    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getInfo() {
        return Info;
    }

    public void setInfo(String info) {
        Info = info;
    }

    public String getError() {
        return Error;
    }

    public void setError(String error) {
        Error = error;
    }

    protected String flag;
    protected String Info;
    protected String Error;

}
