package com.dfc.agsolutions.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RequestBodyData {
    @SerializedName("service_date")
    private String serviceDate;

    @SerializedName("service_year")
    private String serviceYear;

    @SerializedName("service_truck_no")
    private String serviceTruckNo;

    @SerializedName("service_garage")
    private String serviceGarage;

    @SerializedName("service_km")
    private String serviceKm;

    @SerializedName("service_amount")
    private String serviceAmount;

    @SerializedName("service_count")
    private String serviceCount;

    @SerializedName("service_remarks")
    private String serviceRemarks;

    @SerializedName("service_sub_data")
    private ArrayList<ServiceSubData> serviceSubData;

    public RequestBodyData(
            String serviceDate,
            String serviceYear,
            String serviceTruckNo,
            String serviceGarage,
            String serviceKm,
            String serviceAmount,
            String serviceCount,
            String serviceRemarks,
            ArrayList<ServiceSubData> serviceSubData
    ) {
        this.serviceDate = serviceDate;
        this.serviceYear = serviceYear;
        this.serviceTruckNo = serviceTruckNo;
        this.serviceGarage = serviceGarage;
        this.serviceKm = serviceKm;
        this.serviceAmount = serviceAmount;
        this.serviceCount = serviceCount;
        this.serviceRemarks = serviceRemarks;
        this.serviceSubData = serviceSubData;
    }
}