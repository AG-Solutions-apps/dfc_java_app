package com.dfc.agsolutions.model;

import java.util.ArrayList;

public class TodoListDataModel {

    private ArrayList<TodoListDataModel> data;

    public ArrayList<TodoListDataModel> getData() {
        return data;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public void setData(ArrayList<TodoListDataModel> data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }
    String code;

    String id;
    String todo_date;
    String todo_description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTodo_date() {
        return todo_date;
    }

    public void setTodo_date(String todo_date) {
        this.todo_date = todo_date;
    }

    public String getTodo_description() {
        return todo_description;
    }

    public void setTodo_description(String todo_description) {
        this.todo_description = todo_description;
    }
}