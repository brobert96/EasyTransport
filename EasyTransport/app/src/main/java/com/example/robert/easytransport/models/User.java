package com.example.robert.easytransport.models;

/**
 * Created by Robert on 10/4/2017.
 */

public class User {

    private int uid;
    private String name;
    private String password;

    public User(){

    }
    public User(String name, String password){
        this.name = name;
        this.password = password;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
