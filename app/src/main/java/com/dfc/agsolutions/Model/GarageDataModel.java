package com.dfc.agsolutions.Model;

import java.util.ArrayList;

public class GarageDataModel {

    private ArrayList<GarageDataModel> data;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    String code;

    public ArrayList<GarageDataModel> getData() {
        return data;
    }

    public void setData(ArrayList<GarageDataModel> data) {
        this.data = data;
    }

    String vendor_name;

    public String getVendor_name() {
        return vendor_name;
    }

    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }
}
