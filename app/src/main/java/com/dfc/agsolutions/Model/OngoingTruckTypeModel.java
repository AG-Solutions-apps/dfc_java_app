package com.dfc.agsolutions.Model;

import java.util.ArrayList;

public class OngoingTruckTypeModel {
    private ArrayList<OngoingTruckTypeModel> data;

    String id;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    String code;
    String trip_vehicle;

    public String getTrip_vehicle() {
        return trip_vehicle;
    }

    public void setTrip_vehicle(String trip_vehicle) {
        this.trip_vehicle = trip_vehicle;
    }

    public String getTrip_status() {
        return trip_status;
    }

    public void setTrip_status(String trip_status) {
        this.trip_status = trip_status;
    }

    public String getTrip_agency() {
        return trip_agency;
    }

    public void setTrip_agency(String trip_agency) {
        this.trip_agency = trip_agency;
    }

    public String getTrip_date() {
        return trip_date;
    }

    public void setTrip_date(String trip_date) {
        this.trip_date = trip_date;
    }

    public String getTrip_driver() {
        return trip_driver;
    }

    public void setTrip_driver(String trip_driver) {
        this.trip_driver = trip_driver;
    }

    public String getTrip_km() {
        return trip_km;
    }

    public void setTrip_km(String trip_km) {
        this.trip_km = trip_km;
    }

    String trip_status;
    String trip_agency;
    String trip_date;
    String trip_driver;
    String trip_km;
    String trip_bhsd;
    String trip_hsd;
    String trip_advance;
    String trip_hsd_supplied;
    String trip_remarks;

    public String getTrip_bhsd() {
        return trip_bhsd;
    }

    public void setTrip_bhsd(String trip_bhsd) {
        this.trip_bhsd = trip_bhsd;
    }

    public String getTrip_hsd() {
        return trip_hsd;
    }

    public void setTrip_hsd(String trip_hsd) {
        this.trip_hsd = trip_hsd;
    }

    public String getTrip_advance() {
        return trip_advance;
    }

    public void setTrip_advance(String trip_advance) {
        this.trip_advance = trip_advance;
    }

    public String getTrip_hsd_supplied() {
        return trip_hsd_supplied;
    }

    public void setTrip_hsd_supplied(String trip_hsd_supplied) {
        this.trip_hsd_supplied = trip_hsd_supplied;
    }

    public String getTrip_remarks() {
        return trip_remarks;
    }

    public void setTrip_remarks(String trip_remarks) {
        this.trip_remarks = trip_remarks;
    }



    public ArrayList<OngoingTruckTypeModel> getData() {
        return data;
    }

    public void setData(ArrayList<OngoingTruckTypeModel> data) {
        this.data = data;
    }





}