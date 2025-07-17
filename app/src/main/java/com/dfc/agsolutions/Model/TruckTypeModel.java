package com.dfc.agsolutions.Model;

import java.util.ArrayList;

public class TruckTypeModel {
    private ArrayList<TruckTypeModel> data;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    String code;
    String reg_no;
    String trip_date;

    public String getReg_no() {
        return reg_no;
    }

    public void setReg_no(String reg_no) {
        this.reg_no = reg_no;
    }

    public String getTrip_date() {
        return trip_date;
    }

    public void setTrip_date(String trip_date) {
        this.trip_date = trip_date;
    }

    public String getVehicle_status() {
        return vehicle_status;
    }

    public void setVehicle_status(String vehicle_status) {
        this.vehicle_status = vehicle_status;
    }

    String vehicle_status;


    public ArrayList<TruckTypeModel> getData() {
        return data;
    }

    public void setData(ArrayList<TruckTypeModel> data) {
        this.data = data;
    }





}
