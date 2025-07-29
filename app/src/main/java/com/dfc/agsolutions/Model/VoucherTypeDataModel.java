package com.dfc.agsolutions.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class VoucherTypeDataModel {

    private ArrayList<VoucherTypeDataModel> data;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    String code;

    public ArrayList<VoucherTypeDataModel> getData() {
        return data;
    }

    public void setData(ArrayList<VoucherTypeDataModel> data) {
        this.data = data;
    }

    String voucher_type;

    public String getVoucher_type() {
        return voucher_type;
    }

    public void setVoucher_type(String voucher_type) {
        this.voucher_type = voucher_type;
    }
}