package com.dfc.agsolutions.model;

import com.google.gson.annotations.SerializedName;

public class ServiceStatusDataModel {

    String code;
    String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public UserData getData() {
        return data;
    }

    public void setData(UserData data) {
        this.data = data;
    }

    @SerializedName("data")
    private ServiceStatusDataModel.UserData data;


    public static class UserData {

        @SerializedName("service_amount")
        String service_amount;

        public String getService_amount() {
            return service_amount;
        }

        public void setService_amount(String service_amount) {
            this.service_amount = service_amount;
        }

        public String getService_ref() {
            return service_ref;
        }

        public void setService_ref(String service_ref) {
            this.service_ref = service_ref;
        }

        @SerializedName("service_ref")
        String service_ref;

    }
}