package com.bhd.accesscontrol.bean;

public class GroupName {

    private String groupNames;
    private String groupIp;


    public String getGroupIp() {
        return groupIp;
    }

    public void setGroupIp(String groupIp) {
        this.groupIp = groupIp;
    }

    public GroupName(String groupIp, String groupNames) {
        this.groupNames = groupNames;
        this.groupIp = groupIp;
    }

    public String getGroupNames() {
        return groupNames;
    }

    public void setGroupNames(String groupNames) {
        this.groupNames = groupNames;
    }

    @Override
    public String toString() {
        return "GroupName{" +
                "groupNames='" + groupNames + '\'' +
                ", groupIp='" + groupIp + '\'' +
                '}';
    }
}
