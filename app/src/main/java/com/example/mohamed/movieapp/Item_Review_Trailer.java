package com.example.mohamed.movieapp;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Bayome on 12/25/2015.
 */
public class Item_Review_Trailer extends ArrayList implements twoLineInterface{
    public String type;
    public String name;
    public String desc;

    public Item_Review_Trailer(){
        super();
    }


    public Item_Review_Trailer(String type,String name ,String desc){
        super();
        this.type=type;
        this.name=name;
        this.desc=desc;
    }


    @Override
    public String GetType() {
        return type;
    }

    @Override
    public String GetName(){
        return name;
    }

    @Override
    public String GetDescription() {
        return desc;
    }



}
