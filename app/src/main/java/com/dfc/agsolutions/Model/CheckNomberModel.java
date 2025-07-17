package com.dfc.agsolutions.Model;

import com.google.gson.annotations.SerializedName;

public class CheckNomberModel {
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
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

    @SerializedName("code")
    private int code;

    @SerializedName("msg")
    private String msg;

    @SerializedName("data")
    private UserData data;
    public static class UserData {
        // Define fields for user data
        // ...
        @SerializedName("mobile")
        private String mobile;

        public String getFull_name() {
            return full_name;
        }

        public void setFull_name(String full_name) {
            this.full_name = full_name;
        }

        // ...
        @SerializedName("full_name")
        private String full_name;


        @SerializedName("user_branch")
        String user_branch;

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getUser_branch() {
            return user_branch;
        }

        public void setUser_branch(String user_branch) {
            this.user_branch = user_branch;
        }

        public String getCpassword() {
            return cpassword;
        }

        public void setCpassword(String cpassword) {
            this.cpassword = cpassword;
        }

        @SerializedName("cpassword")
        String cpassword;

        public String getUser_type_id() {
            return user_type_id;
        }

        public void setUser_type_id(String user_type_id) {
            this.user_type_id = user_type_id;
        }

        @SerializedName("user_type_id")
        String user_type_id;

    }

}
