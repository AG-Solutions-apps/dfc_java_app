package com.dfc.agsolutions.model;

import com.google.gson.annotations.SerializedName;

public class ProfileModel {

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ProfileModel.Profile getData() {
        return data;
    }

    public void setData(ProfileModel.Profile data) {
        this.data = data;
    }

    @SerializedName("code")
    private int code;
    @SerializedName("data")
    private ProfileModel.Profile data;


    public static class Profile{

        String full_name;
        String mobile;
        String email;
        String vehicle_type;
        String user_address;
        String dl_no;
        String dl_expiry;
        String hazard_lice_no;
        String hazard_lice_expiry;
        String user_image;


        public String getFull_name() {
            return full_name;
        }

        public void setFull_name(String full_name) {
            this.full_name = full_name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getVehicle_type() {
            return vehicle_type;
        }

        public void setVehicle_type(String vehicle_type) {
            this.vehicle_type = vehicle_type;
        }

        public String getUser_address() {
            return user_address;
        }

        public void setUser_address(String user_address) {
            this.user_address = user_address;
        }

        public String getDl_no() {
            return dl_no;
        }

        public void setDl_no(String dl_no) {
            this.dl_no = dl_no;
        }

        public String getDl_expiry() {
            return dl_expiry;
        }

        public void setDl_expiry(String dl_expiry) {
            this.dl_expiry = dl_expiry;
        }

        public String getHazard_lice_no() {
            return hazard_lice_no;
        }

        public void setHazard_lice_no(String hazard_lice_no) {
            this.hazard_lice_no = hazard_lice_no;
        }

        public String getHazard_lice_expiry() {
            return hazard_lice_expiry;
        }

        public void setHazard_lice_expiry(String hazard_lice_expiry) {
            this.hazard_lice_expiry = hazard_lice_expiry;
        }

        public String getUser_image() {
            return user_image;
        }

        public void setUser_image(String user_image) {
            this.user_image = user_image;
        }
    }

}