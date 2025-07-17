package com.dfc.agsolutions.Model;

import java.util.ArrayList;

public class FatchVendorDataModel {
    public ArrayList<FatchVendorDataModel> getData() {
        return data;
    }

    public void setData(ArrayList<FatchVendorDataModel> data) {
        this.data = data;
    }

    private ArrayList<FatchVendorDataModel> data;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    String code;

    public String getVendor_name() {
        return vendor_name;
    }

    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }

    String vendor_name;




}
