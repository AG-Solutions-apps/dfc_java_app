package com.dfc.agsolutions.model;

import java.util.ArrayList;

public class FetchVendorDataModel {

    public ArrayList<FetchVendorDataModel> getData() {
        return data;
    }

    public void setData(ArrayList<FetchVendorDataModel> data) {
        this.data = data;
    }

    private ArrayList<FetchVendorDataModel> data;

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