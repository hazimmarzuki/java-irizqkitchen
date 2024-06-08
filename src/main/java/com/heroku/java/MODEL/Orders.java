package com.heroku.java.MODEL;

import java.sql.Date;

public class Orders {

    public int orderid;
    public Date orderdate;
    public int orderprice;
    public String orderstatus;

    // default constructor
    public Orders() {

    }

    public Orders(int orderid, Date orderdate, int orderprice, String orderstatus) {
        this.orderid = orderid;
        this.orderdate = orderdate;
        this.orderprice = orderprice;
        this.orderstatus = orderstatus;
    }

    public int getOrderid() {
        return this.orderid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public Date getOrderdate() {
        return this.orderdate;
    }

    public void setOrderdate(Date orderdate) {
        this.orderdate = orderdate;
    }

    public int getOrderprice() {
        return this.orderprice;
    }

    public void setOrderprice(int orderprice) {
        this.orderprice = orderprice;
    }

    public String getOrderstatus() {
        return this.orderstatus;
    }

    public void setOrderstatus(String orderstatus) {
        this.orderstatus = orderstatus;
    }

}
