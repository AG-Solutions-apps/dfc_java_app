package com.dfc.agsolutions.Model;

import java.util.ArrayList;
import java.util.List;

public class FatchVhicalDataModel {
    public ArrayList<FatchVhicalDataModel> getData() {
        return data;
    }

    public void setData(ArrayList<FatchVhicalDataModel> data) {
        this.data = data;
    }

    private ArrayList<FatchVhicalDataModel> data;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    String code;
    String reg_no;

    public String getReg_no() {
        return reg_no;
    }

    public void setReg_no(String reg_no) {
        this.reg_no = reg_no;
    }

    public String getVehicle_driver() {
        return vehicle_driver;
    }

    public void setVehicle_driver(String vehicle_driver) {
        this.vehicle_driver = vehicle_driver;
    }

    public String getVehicle_mileage() {
        return vehicle_mileage;
    }

    public void setVehicle_mileage(String vehicle_mileage) {
        this.vehicle_mileage = vehicle_mileage;
    }

    String vehicle_driver;
    String vehicle_mileage;

}
