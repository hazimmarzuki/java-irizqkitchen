package com.heroku.java.MODEL;

import org.springframework.web.multipart.MultipartFile;

public class Cakes extends Products {
    public int cakesize;

    public Cakes() {

    }

    public Cakes(int proid, String proname, String protype, int proprice, byte[] proimg, MultipartFile proimgs,
            String proimage, int cakesize) {
        super(proid, proname, protype, proprice, proimg, proimgs, proimage);
        this.cakesize = cakesize;
    }

    public int getCakesize() {
        return this.cakesize;
    }

    public void setCakesize(int cakesize) {
        this.cakesize = cakesize;
    }

}
