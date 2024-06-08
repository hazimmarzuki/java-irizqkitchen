package com.heroku.java.MODEL;

public class User {
    public int userid;
    public String fullname;
    public String email;
    public String password;

    public User() {

    }

    public User(int userid, String fullname, String email, String password) {
        this.userid = userid;
        this.fullname = fullname;
        this.email = email;
        this.password = password;
    }

    public int getUserid() {
        return this.userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getFullname() {
        return this.fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
