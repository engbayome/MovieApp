package com.example.mohamed.movieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Bayome on 12/23/2015.
 */
public class CustomArrayAdapter extends ArrayAdapter<Item> {

    Context context;
    //int resource;
    Item objects[] = null;


    public CustomArrayAdapter(Context context, int resource, Item[] objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        DataHolder holder ;

        if(row == null)
        {
            row = LayoutInflater.from(getContext()).inflate(
                    R.layout.item, parent, false);

            holder = new DataHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.image_main_item);
            holder.txtTitle = (TextView)row.findViewById(R.id.title_movie);

            row.setTag(holder);
        }else
        {
            holder = (DataHolder)row.getTag();
        }

        Item item = objects[position];
        holder.txtTitle.setText(item.title);
        Picasso.with(context).load(item.image).into(holder.imgIcon);


        return row;
    }




    static class DataHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
    }
}
