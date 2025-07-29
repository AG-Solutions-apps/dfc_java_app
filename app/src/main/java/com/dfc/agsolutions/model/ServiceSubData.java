package com.dfc.agsolutions.model;

import com.google.gson.annotations.SerializedName;

public class ServiceSubData {

    @SerializedName("service_sub_type")
    private String serviceSubType;

    @SerializedName("service_sub_amount")
    private String serviceSubAmount;

    public ServiceSubData(String serviceSubType, String serviceSubAmount) {
        this.serviceSubType = serviceSubType;
        this.serviceSubAmount = serviceSubAmount;
    }
}