package com.dfc.agsolutions.Model;

import java.util.ArrayList;

public class DriverListDataModel {

    private ArrayList<DriverListDataModel> data;

    public ArrayList<DriverListDataModel> getData() {
        return data;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public void setData(ArrayList<DriverListDataModel> data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDl_expiry() {
        return dl_expiry;
    }

    public void setDl_expiry(String dl_expiry) {
        this.dl_expiry = dl_expiry;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    String code;
    String full_name;
    String mobile;
    String dl_expiry;
    String user_status;
    String user_image;

}