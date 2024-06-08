package com.heroku.java.MODEL;

public class staff extends User {

    public String staffsrole;

    public staff() {

    }

    public staff(int userid, String fullname, String email, String password, String staffsrole) {
        super(userid, fullname, email, password);
        this.staffsrole = staffsrole;
    }

    public String getStaffsrole() {
        return this.staffsrole;
    }

    public void setStaffsrole(String staffsrole) {
        this.staffsrole = staffsrole;
    }

}
