package com.example.mohamed.movieapp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Bayome on 1/9/2016.
 */
public class DetailParse {



    public String[] parse(String JsonStr)
            throws JSONException {


        // These are the names of the JSON objects that need to be extracted.
        final String TITLE = "title";
        final String IMG_PATH = "poster_path";
        final String RELEASE_DATA = "release_date";
        final String RATING = "vote_average";
        final String OVERVIEW = "overview";
        final String ID = "id";

        JSONObject movieData = new JSONObject(JsonStr);


        String id;
        String title;
        String img_path;
        String release_date;
        String rating;
        String overview;


        id = movieData.getString(ID);
        title = movieData.getString(TITLE);
        img_path = movieData.getString(IMG_PATH);
        img_path =("http://image.tmdb.org/t/p/w185"+img_path).toString();
        release_date = movieData.getString(RELEASE_DATA);
        rating = movieData.getString(RATING);
        overview = movieData.getString(OVERVIEW);


        String[] resultStrs = {title,img_path, release_date, rating, overview,id};

        return resultStrs;

    }


}
