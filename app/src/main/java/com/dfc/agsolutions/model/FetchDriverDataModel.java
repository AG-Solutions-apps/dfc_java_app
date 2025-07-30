package com.dfc.agsolutions.model;

import java.util.ArrayList;

public class FetchDriverDataModel {

    public ArrayList<FetchDriverDataModel> getData() {
        return data;
    }

    public void setData(ArrayList<FetchDriverDataModel> data) {
        this.data = data;
    }

    private ArrayList<FetchDriverDataModel> data;


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