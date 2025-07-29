package com.dfc.agsolutions.model;

import java.util.ArrayList;

public class ServicesubFinalModel {

    private String code;
    private String msg;
    private ArrayList<CreatServicaeListDataModel> data;
    private String status;
    private String error;
    private String error_code;
    private String error_msg;
    private String error_status;
    private String error_data;
    private String error_message;

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
    public ArrayList<CreatServicaeListDataModel> getData() {
        return data;
    }
    public void setData(ArrayList<CreatServicaeListDataModel> data) {
        this.data = data;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
    public String getError_code() {
        return error_code;
    }
    public void setError_code(String error_code) {
        this.error_code = error_code;
    }
    public String getError_msg() {
        return error_msg;
    }
    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }
    public String getError_status() {
        return error_status;
    }
    public void setError_status(String error_status) {
        this.error_status = error_status;
    }
    public String getError_data() {
        return error_data;
    }
    public void setError_data(String error_data) {
        this.error_data = error_data;
    }
    public String getError_message() {
        return error_message;
    }
    public void setError_message(String error_message) {
        this.error_message = error_message;
    }

}