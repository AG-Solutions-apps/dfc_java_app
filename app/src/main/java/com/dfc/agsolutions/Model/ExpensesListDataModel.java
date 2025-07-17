package com.dfc.agsolutions.Model;

import java.util.ArrayList;

public class ExpensesListDataModel {

    private ArrayList<ExpensesListDataModel> data;
//    String totalAmount;
    String totalReceived;

    public String getTotalExpensive() {
        return totalExpensive;
    }

    public void setTotalExpensive(String totalExpensive) {
        this.totalExpensive = totalExpensive;
    }

    String totalExpensive;

//    public String getTotalAmount() {
//        return totalAmount;
//    }

//    public void setTotalAmount(String totalAmount) {
//        this.totalAmount = totalAmount;
//    }

    public String getTotalReceived() {
        return totalReceived;
    }

    public void setTotalReceived(String totalReceived) {
        this.totalReceived = totalReceived;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    String code;

    public ArrayList<ExpensesListDataModel> getData() {
        return data;
    }

    public void setData(ArrayList<ExpensesListDataModel> data) {
        this.data = data;
    }

    int id;
    String payment_details_date;
    String payment_details_voucher_type;
    String payment_details_debit;
    String payment_details_amount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPayment_details_date() {
        return payment_details_date;
    }

    public void setPayment_details_date(String payment_details_date) {
        this.payment_details_date = payment_details_date;
    }

    public String getPayment_details_voucher_type() {
        return payment_details_voucher_type;
    }

    public void setPayment_details_voucher_type(String payment_details_voucher_type) {
        this.payment_details_voucher_type = payment_details_voucher_type;
    }

    public String getPayment_details_debit() {
        return payment_details_debit;
    }

    public void setPayment_details_debit(String payment_details_debit) {
        this.payment_details_debit = payment_details_debit;
    }

    public String getPayment_details_amount() {
        return payment_details_amount;
    }

    public void setPayment_details_amount(String payment_details_amount) {
        this.payment_details_amount = payment_details_amount;
    }
}
