package com.dfc.agsolutions.Model;

import com.google.gson.annotations.SerializedName;

public class TripCurrantDataModel {
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public TripCurrantDataModel.UserData1 getData() {
        return data;
    }

    public void setData(TripCurrantDataModel.UserData1 data) {
        this.data = data;
    }

    @SerializedName("code")
    private int code;
    @SerializedName("data")
    private TripCurrantDataModel.UserData1 data;

    public static class UserData1 {
        String id;
        String trip_vehicle;
        String trip_status;
        String trip_agency;
        String trip_date;
        String trip_driver;
        String trip_km;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

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
    }

}