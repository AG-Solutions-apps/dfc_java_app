package com.dfc.agsolutions.model;

import java.util.List;

public class VehicleHistoryResponse {
    private int code;
    private List<PreviousHistoryDataModel> data;

    // Getters and Setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<PreviousHistoryDataModel> getData() {
        return data;
    }

    public void setData(List<PreviousHistoryDataModel> data) {
        this.data = data;
    }

    // Inner Model Class
    public static class PreviousHistoryDataModel {
        private String trip_vehicle;
        private String trip_status;
        private String trip_agency;
        private String trip_date;
        private String trip_driver;
        private int trip_km;

        // Getters and Setters
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

        public int getTrip_km() {
            return trip_km;
        }

        public void setTrip_km(int trip_km) {
            this.trip_km = trip_km;
        }
    }

}