package com.bhd.accesscontrol.bean;

public class InfoBean implements Comparable<InfoBean>{
    //implements Comparable<InfoBean>
    protected String ip;
    //    protected String order;
    protected String door;
    protected String doorName;
    protected String groupname;


    public String getDoorName() {
        return doorName;
    }

    public void setDoorName(String doorName) {
        this.doorName = doorName;
    }


    public String getDoor() {
        return door;
    }

    public void setDoor(String door) {
        this.door = door;
    }

    public InfoBean(String ip) {
        this.ip = ip;
    }

    public InfoBean(String ip, String door, String doorName) {
        this.ip = ip;
        this.door = door;
        this.doorName = doorName;
    }

    public InfoBean(String ip, String door, String doorName,String groupname) {
        this.ip = ip;
        this.door = door;
        this.doorName = doorName;
        this.groupname = groupname;
    }

//    public InfoBean(String ip, String order) {
//        this.ip = ip;
//        this.order = order;
//    }

    public InfoBean() {
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    //    public String getOrder() {
//        return order;
//    }

//    public void setOrder(String order) {
//        this.order = order;
//    }


    @Override
    public String toString() {
        return "InfoBean{" +
                "ip='" + ip + '\'' +
                ", door='" + door + '\'' +
                ", doorName='" + doorName + '\'' +
                ", groupname='" + groupname + '\'' +
                '}';
    }

    @Override
    public int compareTo(InfoBean infoBean) {
        return -Integer.parseInt(infoBean.groupname) + Integer.parseInt(this.groupname);
    }
}
