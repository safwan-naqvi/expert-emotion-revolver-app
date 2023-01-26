package com.example.socion.HelpingClasses;

public class User {

    public String name,password,phone,profile,uid;

    public User(){}

    public User(String name, String password, String phone, String profile, String uid) {
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.profile = profile;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
