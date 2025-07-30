package com.dfc.agsolutions.model;

import java.util.ArrayList;

public class FetchAgencyDataModel {

    public ArrayList<FetchAgencyDataModel> getData() {
        return data;
    }

    public void setData(ArrayList<FetchAgencyDataModel> data) {
        this.data = data;
    }

    private ArrayList<FetchAgencyDataModel> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    String code;

    String agency_name;

    public String getAgency_name() {
        return agency_name;
    }

    public void setAgency_name(String agency_name) {
        this.agency_name = agency_name;
    }

    public String getAgency_rt_km() {
        return agency_rt_km;
    }

    public void setAgency_rt_km(String agency_rt_km) {
        this.agency_rt_km = agency_rt_km;
    }

    String agency_rt_km;

}