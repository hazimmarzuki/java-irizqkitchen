package com.heroku.java.MODEL;

import java.sql.Date;

import org.springframework.web.multipart.MultipartFile;

public class payments {

    public int paymentid;
    public int paymentamt;
    public Date paymentdate;
    public byte[] paymentproof;
    public MultipartFile paymentproofs;
    public String paymentprf;

    public payments() {

    }

    public payments(int paymentid, int paymentamt, Date paymentdate, byte[] paymentproof, MultipartFile paymentproofs,
            String paymentprf) {
        this.paymentid = paymentid;
        this.paymentamt = paymentamt;
        this.paymentdate = paymentdate;
        this.paymentproof = paymentproof;
        this.paymentproofs = paymentproofs;
        this.paymentprf = paymentprf;
    }

    public int getPaymentid() {
        return this.paymentid;
    }

    public void setPaymentid(int paymentid) {
        this.paymentid = paymentid;
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

}
