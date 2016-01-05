package com.example.mohamed.movieapp;

/**
 * Created by Bayome on 12/23/2015.
 */
public class Item {
    public String id;
    public String image;
    public String title;


    public Item(){
        super();
    }

    public Item(String id,String icon,String title) {
        super();
        this.id = id;
        this.image = icon;
        this.title = title;
    }
}
