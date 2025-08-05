package com.dfc.agsolutions.model;

import com.google.gson.annotations.SerializedName;

public class VehicleDetailsModel {

    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private VehicleData data;

    // Getter and Setter
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public VehicleData getData() {
        return data;
    }

    public void setData(VehicleData data) {
        this.data = data;
    }

    public static class VehicleData {

        @SerializedName("reg_no")
        private String regNo;

        @SerializedName("vehicle_branch")
        private String vehicleBranch;

        @SerializedName("vehicle_company")
        private String vehicleCompany;

        @SerializedName("vehicle_type")
        private String vehicleType;

        @SerializedName("mfg_year")
        private String mfgYear;

        @SerializedName("ins_due")
        private String insDue;

        @SerializedName("permit_due")
        private String permitDue;

        @SerializedName("fc_due")
        private String fcDue;

        @SerializedName("vehicle_mileage")
        private double vehicleMileage;

        // Getters and Setters
        public String getRegNo() {
            return regNo;
        }

        public void setRegNo(String regNo) {
            this.regNo = regNo;
        }

        public String getVehicleBranch() {
            return vehicleBranch;
        }

        public void setVehicleBranch(String vehicleBranch) {
            this.vehicleBranch = vehicleBranch;
        }

        public String getVehicleCompany() {
            return vehicleCompany;
        }

        public void setVehicleCompany(String vehicleCompany) {
            this.vehicleCompany = vehicleCompany;
        }

        public String getVehicleType() {
            return vehicleType;
        }

        public void setVehicleType(String vehicleType) {
            this.vehicleType = vehicleType;
        }

        public String getMfgYear() {
            return mfgYear;
        }

        public void setMfgYear(String mfgYear) {
            this.mfgYear = mfgYear;
        }

        public String getInsDue() {
            return insDue;
        }

        public void setInsDue(String insDue) {
            this.insDue = insDue;
        }

        public String getPermitDue() {
            return permitDue;
        }

        public void setPermitDue(String permitDue) {
            this.permitDue = permitDue;
        }

        public String getFcDue() {
            return fcDue;
        }

        public void setFcDue(String fcDue) {
            this.fcDue = fcDue;
        }

        public double getVehicleMileage() {
            return vehicleMileage;
        }

        public void setVehicleMileage(double vehicleMileage) {
            this.vehicleMileage = vehicleMileage;
        }
    }

}