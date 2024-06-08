package com.heroku.java.MODEL;

public class Orderdetails {

    public int proid;
    public int orderid;
    public int orderdetailsqty;

    public Orderdetails() {
    }

    public Orderdetails(int proid, int orderid, int orderdetailsqty) {
        this.proid = proid;
        this.orderid = orderid;
        this.orderdetailsqty = orderdetailsqty;
    }

    public int getProid() {
        return this.proid;
    }

    public void setProid(int proid) {
        this.proid = proid;
    }

    public int getOrderid() {
        return this.orderid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public int getOrderdetailsqty() {
        return this.orderdetailsqty;
    }

    public void setOrderdetailsqty(int orderdetailsqty) {
        this.orderdetailsqty = orderdetailsqty;
    }

}
