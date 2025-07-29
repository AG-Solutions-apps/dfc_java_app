package com.dfc.agsolutions.Model;

import java.util.ArrayList;

public class FatchDriverDataModel {
    public ArrayList<FatchDriverDataModel> getData() {
        return data;
    }

    public void setData(ArrayList<FatchDriverDataModel> data) {
        this.data = data;
    }

    private ArrayList<FatchDriverDataModel> data;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    String code;

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    String full_name;

}