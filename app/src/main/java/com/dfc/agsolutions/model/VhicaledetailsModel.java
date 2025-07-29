package com.dfc.agsolutions.model;

import com.google.gson.annotations.SerializedName;

public class VhicaledetailsModel {
    @SerializedName("code")
    private Integer code;
    @SerializedName("data")
    private DataModel data;
    public Integer getCode() {
        return code;
    }
    public void setCode(Integer code) {
        this.code = code;
    }
    public DataModel getData() {
        return data;
    }
    public void setData(DataModel data) {
        this.data = data;
    }

    public class DataModel {
        @SerializedName("vehicle_branch")
        private String vehicleBranch;
        @SerializedName("vehicle_company")
        private String vehicleCompany;
        @SerializedName("vehicle_type")
        private String vehicleType;
        @SerializedName("manufacturing_year")
        private String manufacturingYear;
        @SerializedName("insurance_due")
        private String insuranceDue;
        @SerializedName("permit_due")
        private String permitDue;
        @SerializedName("fc_due")
        private String fcDue;
        @SerializedName("vehicle_mileage")
        private String vehicleMileage;
        @SerializedName("reg_no")
        private String regNo;
        public String getRegNo() {
            return regNo;
        }
        public void setRegNo(String regNo) {
            this.regNo = regNo;
        }
        public String getFcDue() {
            return fcDue;
        }
        public void setFcDue(String fcDue) {
            this.fcDue = fcDue;
        }
        public String getVehicleMileage() {
            return vehicleMileage;
        }
        public void setVehicleMileage(String vehicleMileage) {
            this.vehicleMileage = vehicleMileage;
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
        public String getManufacturingYear() {
            return manufacturingYear;
        }
        public void setManufacturingYear(String manufacturingYear) {
            this.manufacturingYear = manufacturingYear;
        }
        public String getInsuranceDue() {
            return insuranceDue;
        }
        public void setInsuranceDue(String insuranceDue) {
            this.insuranceDue = insuranceDue;
        }
        public String getPermitDue() {
            return permitDue;
        }
        public void setPermitDue(String permitDue) {
            this.permitDue = permitDue;
        }
    }

}