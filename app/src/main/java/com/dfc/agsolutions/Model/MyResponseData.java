package com.dfc.agsolutions.Model;

import com.google.gson.annotations.SerializedName;

public class MyResponseData {
    @SerializedName("code")
    private int code;

    @SerializedName("msg")
    private String msg;

    @SerializedName("data")
    private Data data;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        @SerializedName("token")
        private String token;

        @SerializedName("user")
        private User user;

        public String getToken() {
            return token;
        }

        public User getUser() {
            return user;
        }
    }

    public static class User {
        @SerializedName("id")
        private int id;

        @SerializedName("full_name")
        private String fullName;

        @SerializedName("name")
        private String name;

        @SerializedName("email")
        private String email;

        @SerializedName("user_status")
        private String user_status;

        public String getUser_status() {
            return user_status;
        }

        public void setUser_status(String user_status) {
            this.user_status = user_status;
        }

        // Add other fields as needed

        public int getId() {
            return id;
        }

        public String getFullName() {
            return fullName;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        // Implement getters for other fields
    }
}