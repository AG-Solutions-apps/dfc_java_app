package com.dfc.agsolutions.model;

import java.util.ArrayList;

public class FetchBHSDDataModel {

    public ArrayList<FetchBHSDDataModel> getData() {
        return data;
    }

    public void setData(ArrayList<FetchBHSDDataModel> data) {
        this.data = data;
    }

    private ArrayList<FetchBHSDDataModel> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    String code;

    public String getTrip_hsd() {
        return trip_hsd;
    }

    public void setTrip_hsd(String trip_hsd) {
        this.trip_hsd = trip_hsd;
    }

    String trip_hsd;

    public String getTrip_hsd_supplied() {
        return trip_hsd_supplied;
    }

    public void setTrip_hsd_supplied(String trip_hsd_supplied) {
        this.trip_hsd_supplied = trip_hsd_supplied;
    }

    String trip_hsd_supplied;

}