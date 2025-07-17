package com.dfc.agsolutions.Model;

import java.util.ArrayList;

public class ServiceTypeDataModel {


    private ArrayList<ServiceTypeDataModel> data;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    String code;

    public ArrayList<ServiceTypeDataModel> getData() {
        return data;
    }

    public void setData(ArrayList<ServiceTypeDataModel> data) {
        this.data = data;
    }

    String service_types;

    public String getService_types() {
        return service_types;
    }

    public void setService_types(String service_types) {
        this.service_types = service_types;
    }
}
