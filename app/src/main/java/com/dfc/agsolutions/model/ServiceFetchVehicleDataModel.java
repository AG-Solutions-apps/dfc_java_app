package com.dfc.agsolutions.model;

import java.util.ArrayList;

public class ServiceFetchVehicleDataModel {

    private ArrayList<ServiceFetchVehicleDataModel> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    String code;

    public ArrayList<ServiceFetchVehicleDataModel> getData() {
        return data;
    }

    public void setData(ArrayList<ServiceFetchVehicleDataModel> data) {
        this.data = data;
    }

    public String getReg_no() {
        return reg_no;
    }

    public void setReg_no(String reg_no) {
        this.reg_no = reg_no;
    }

    String reg_no;

}