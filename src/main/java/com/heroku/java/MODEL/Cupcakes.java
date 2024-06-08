package com.heroku.java.MODEL;

import org.springframework.web.multipart.MultipartFile;

public class Cupcakes extends Products {
    public String cuptoppings;

    public Cupcakes() {

    }

    public Cupcakes(int proid, String proname, String protype, int proprice, byte[] proimg, MultipartFile proimgs,
            String proimage, String cuptoppings) {
        super(proid, proname, protype, proprice, proimg, proimgs, proimage);
        this.cuptoppings = cuptoppings;
    }

    public String getCuptoppings() {
        return this.cuptoppings;
    }

    public void setCuptoppings(String cuptoppings) {
        this.cuptoppings = cuptoppings;
    }

}
