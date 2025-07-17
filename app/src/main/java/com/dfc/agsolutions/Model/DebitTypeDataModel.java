package com.dfc.agsolutions.Model;

import java.util.ArrayList;

public class DebitTypeDataModel {

    private ArrayList<DebitTypeDataModel> data;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    String code;

    public ArrayList<DebitTypeDataModel> getData() {
        return data;
    }

    public void setData(ArrayList<DebitTypeDataModel> data) {
        this.data = data;
    }

    String common_name;

    public String getCommon_name() {
        return common_name;
    }

    public void setCommon_name(String common_name) {
        this.common_name = common_name;
    }
}
