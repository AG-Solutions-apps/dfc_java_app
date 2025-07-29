package com.dfc.agsolutions.Model;

import java.util.ArrayList;

public class CreatServicaeListDataModel {

    private ArrayList<CreatServicaeListDataModel> data;
    String id;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    String code;
    String msg;
    String temp_service_sub_ref;
    String temp_service_sub_type;

    public ArrayList<CreatServicaeListDataModel> getData() {
        return data;
    }

    public void setData(ArrayList<CreatServicaeListDataModel> data) {
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemp_service_sub_ref() {
        return temp_service_sub_ref;
    }

    public void setTemp_service_sub_ref(String temp_service_sub_ref) {
        this.temp_service_sub_ref = temp_service_sub_ref;
    }

    public String getTemp_service_sub_type() {
        return temp_service_sub_type;
    }

    public void setTemp_service_sub_type(String temp_service_sub_type) {
        this.temp_service_sub_type = temp_service_sub_type;
    }

    public String getTemp_service_sub_amount() {
        return temp_service_sub_amount;
    }

    public void setTemp_service_sub_amount(String temp_service_sub_amount) {
        this.temp_service_sub_amount = temp_service_sub_amount;
    }

    String temp_service_sub_amount;

}