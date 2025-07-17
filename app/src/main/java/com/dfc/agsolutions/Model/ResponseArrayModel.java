package com.dfc.agsolutions.Model;

import java.util.List;

public class ResponseArrayModel {
    private List<Branch> data;

    public List<Branch> getData() {
        return data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    String code;

}
