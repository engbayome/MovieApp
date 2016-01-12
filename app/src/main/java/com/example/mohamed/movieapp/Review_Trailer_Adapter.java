package com.example.mohamed.movieapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Bayome on 12/25/2015.
 */
public class Review_Trailer_Adapter extends ArrayAdapter<Item_Review_Trailer> {
    private final String LOG_TAG = Review_Trailer_Adapter.class.getSimpleName();
    Context context;
    Item_Review_Trailer objects ;

    public Review_Trailer_Adapter(Context context, int resource, Item_Review_Trailer objects) {
        super(context, resource, objects);
        this.context=context;
        this.objects=objects;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Item_Review_Trailer item = (Item_Review_Trailer) objects.get(position);

            View row = convertView;
            review_holder holder = null;


            String type = item.GetType();
            //trailer
            //review
            if (row == null && type == "trailer"){
                row = LayoutInflater.from(getContext()).inflate(
                        R.layout.item_trailer, parent, false);

                holder = new review_holder();
                holder.trailer = (TextView)row.findViewById(R.id.trailer_name);

                holder.trailer.setText(item.name);
                row.setTag(holder);
            }else if (type == "trailer"){
                holder = (review_holder)row.getTag();
                holder.trailer.setText(item.name);

            }else if(row == null && type == "review")
            {
                row = LayoutInflater.from(getContext()).inflate(
                        R.layout.item_review, parent, false);

                holder = new review_holder();
                holder.review_author = (TextView)row.findViewById(R.id.review_author);
                holder.review_content = (TextView)row.findViewById(R.id.review_content);

                holder.review_author.setText(item.name);
                holder.review_content.setText(item.desc);

                row.setTag(holder);
            }else if(type == "review")
            {
                holder = (review_holder)row.getTag();
                if (item.name == null){
                    holder.review_author.setText("No Name");
                }else{
                    holder.review_author.setText(item.name);
                }
                holder.review_content.setText(item.desc);

            }


            return row;
    }



    static class review_holder {
        TextView review_author;
        TextView review_content;
        TextView trailer;
    }
}
