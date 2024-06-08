package com.heroku.java.MODEL;

import java.sql.Date;

import org.springframework.web.multipart.MultipartFile;

public class orderstaff {
    public int orderid;
    public String fullname;
    public String custaddress;
    public String proname;
    public int orderdetailsqty;
    public int orderprice;
    public int paymentamt;
    public Date paymentdate;

    public byte[] paymentproof;
    public MultipartFile paymentproofs;
    String paymentprf;

    public String orderstatus;

    public orderstaff() {
    }

    public orderstaff(int orderid, String fullname, String custaddress, String proname, int orderdetailsqty,
            int orderprice, int paymentamt, Date paymentdate, byte[] paymentproof, MultipartFile paymentproofs,
            String paymentprf, String orderstatus) {
        this.orderid = orderid;
        this.fullname = fullname;
        this.custaddress = custaddress;
        this.proname = proname;
        this.orderdetailsqty = orderdetailsqty;
        this.orderprice = orderprice;
        this.paymentamt = paymentamt;
        this.paymentdate = paymentdate;
        this.paymentproof = paymentproof;
        this.paymentproofs = paymentproofs;
        this.paymentprf = paymentprf;
        this.orderstatus = orderstatus;
    }

    public orderstaff(int orderid, String proname, int orderdetailsqty, int orderprice, String orderstatus) {
        this.orderid = orderid;
        this.proname = proname;
        this.orderdetailsqty = orderdetailsqty;
        this.orderprice = orderprice;
        this.orderstatus = orderstatus;
    }

    public int getOrderid() {
        return this.orderid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public String getFullname() {
        return this.fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getCustaddress() {
        return this.custaddress;
    }

    public void setCustaddress(String custaddress) {
        this.custaddress = custaddress;
    }

    public String getProname() {
        return this.proname;
    }

    public void setProname(String proname) {
        this.proname = proname;
    }

    public int getOrderdetailsqty() {
        return this.orderdetailsqty;
    }

    public void setOrderdetailsqty(int orderdetailsqty) {
        this.orderdetailsqty = orderdetailsqty;
    }

    public int getOrderprice() {
        return this.orderprice;
    }

    public void setOrderprice(int orderprice) {
        this.orderprice = orderprice;
    }

    public int getPaymentamt() {
        return this.paymentamt;
    }

    public void setPaymentamt(int paymentamt) {
        this.paymentamt = paymentamt;
    }

    public Date getPaymentdate() {
        return this.paymentdate;
    }

    public void setPaymentdate(Date paymentdate) {
        this.paymentdate = paymentdate;
    }

    public byte[] getPaymentproof() {
        return this.paymentproof;
    }

    public void setPaymentproof(byte[] paymentproof) {
        this.paymentproof = paymentproof;
    }

    public MultipartFile getPaymentproofs() {
        return this.paymentproofs;
    }

    public void setPaymentproofs(MultipartFile paymentproofs) {
        this.paymentproofs = paymentproofs;
    }

    public String getPaymentprf() {
        return this.paymentprf;
    }

    public void setPaymentprf(String paymentprf) {
        this.paymentprf = paymentprf;
    }

    public String getOrderstatus() {
        return this.orderstatus;
    }

    public void setOrderstatus(String orderstatus) {
        this.orderstatus = orderstatus;
    }

}
