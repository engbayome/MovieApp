package com.example.mohamed.movieapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private String id;
    private String image;
    private String json;
    private String Title;
    private String[] detailAdapter;
    private Item_Review_Trailer Adapter;
    ListView list_view;
    int NumOfTrailers;
    DatabaseHelper Database;
    ToggleButton favorite;
    boolean isfounded;


    public DetailActivityFragment(){
    }

    public static DetailActivityFragment newInstance(String json){
        DetailActivityFragment fragment = new DetailActivityFragment();

        Bundle args = new Bundle();
        args.putString("json",json);
        fragment.setArguments(args);

        return fragment;
    }


    public String getShownIndex() {
        return getArguments().getString("json");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);



        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("json")) {
            json = intent.getStringExtra("json");
        }else
        {
            json = getShownIndex();
        }

        DetailParse detailParseObj = new DetailParse();
        try {
            detailAdapter = detailParseObj.parse(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        list_view = (ListView) rootView.findViewById(R.id.listview);
        View header = inflater.inflate(R.layout.header, null ,false);
        list_view.addHeaderView(header);

        ImageView img = (ImageView) header.findViewById(R.id.detail_image_view);
        Picasso.with(getContext()).load(detailAdapter[1]).into(img);
        TextView title = (TextView) header.findViewById(R.id.detail_name_text);
        title.setText(detailAdapter[0]);
        TextView release_date = (TextView) header.findViewById(R.id.release_date);
        release_date.setText(detailAdapter[2]);
        TextView user_rating = (TextView) header.findViewById(R.id.user_rating);
        user_rating.setText(detailAdapter[3]);
        TextView overview = (TextView) header.findViewById(R.id.overview);
        overview.setText(detailAdapter[4]);

        id = detailAdapter[5];
        Title = detailAdapter[0];
        image = detailAdapter[1];

        favorite = (ToggleButton) header.findViewById(R.id.favorit_btn);

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i <= NumOfTrailers){
                    Item_Review_Trailer Item = (Item_Review_Trailer) Adapter.get(i-1);
                    String link = Item.GetDescription();
                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(intent);
                }
            }
        });

        Database = new DatabaseHelper(getContext());
        isfounded =Database.SelectMovie(id);
        if (isfounded){
            favorite.setTextOff("Favorite");
        }else {
            favorite.setTextOn("Un Favorite");
        }
        favorite.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isfounded =Database.SelectMovie(id);
                        if (isfounded){
                            int isdeleted = Database.DeleteMovie(id);
                            if (isdeleted != 0){
                                Toast.makeText(getActivity(),"Data removed from favorited",Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(getActivity(),"Data doesnot from favorited",Toast.LENGTH_LONG).show();
                            }
                            favorite.setTextOff("Favorite");
                        }else
                        {
                            boolean isInserted = Database.InsertMovie(id,image,Title,json);
                            if (isInserted == true){
                                Toast.makeText(getActivity(),"Data favorited",Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(getActivity(),"Data not inserted",Toast.LENGTH_LONG).show();
                            }
                            favorite.setTextOn("Un Favorite");
                        }
                    }
                });
        return rootView;
    }

    private void updateData() {
        Trailer_Async trailer_async = new Trailer_Async();
        trailer_async.execute(id);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateData();
    }


    private class Review_Async extends AsyncTask<String, Void, Item_Review_Trailer[]> {
        private final String LOG_TAG = Review_Async.class.getSimpleName();


        private Item_Review_Trailer[] getReviewDataFromJson(String ReviewJsonStr)
                throws JSONException {

            final String RESULTS = "results";
            final String CONTENT = "content";
            final String AUTHOR = "author";

            JSONObject ReviewdataJson = new JSONObject(ReviewJsonStr);
            JSONArray ReviewdataArray = ReviewdataJson.getJSONArray(RESULTS);

            int numofresults = ReviewdataArray.length();

            Item_Review_Trailer[] resultStrs = new Item_Review_Trailer[numofresults];

            String content;
            String author;

            for (int i = 0; i < numofresults; i++) {


                JSONObject ReviewmovieData = ReviewdataArray.getJSONObject(i);

                author = ReviewmovieData.getString(AUTHOR);
                content = ReviewmovieData.getString(CONTENT);

                Item_Review_Trailer element = new Item_Review_Trailer("review",author, content);

                resultStrs[i] = element;
            }
            return resultStrs;

        }

        @Override
        protected Item_Review_Trailer[] doInBackground(String... strings) {


            if (strings.length == 0) {
                return null;
            }

            String id = strings[0];
            String api_key = "5c1d3fa899d823cdca9845d785e47b7d";

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String dataReviewJsonStr = null;

            try {
                //http://api.themoviedb.org/3/movie/131634/reviews?

                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + id + "/reviews?";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, api_key)
                        .build();

                URL url_review = new URL(builtUri.toString());


                urlConnection = (HttpURLConnection) url_review.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                // Read the input stream into a String
                InputStream inputStream1 = urlConnection.getInputStream();

                StringBuffer buffer1 = new StringBuffer();
                if (inputStream1 == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream1));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer1.append(line + "\n");
                }

                if (buffer1.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                dataReviewJsonStr = buffer1.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getReviewDataFromJson(dataReviewJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Item_Review_Trailer[] result) {

            if (result != null && result.length > 0) {

                for(Item_Review_Trailer Review_Adapter : result) {
                    Adapter.add(Review_Adapter);
                }

                Review_Trailer_Adapter adapter = new Review_Trailer_Adapter(getContext(), R.layout.item_review,  Adapter);
                list_view.setAdapter(adapter);
            }
            // New data is back from the server.  Hooray!
        }


    }


    private class Trailer_Async extends AsyncTask<String, Void, Item_Review_Trailer[]> {
        private final String LOG_TAG = Trailer_Async.class.getSimpleName();



        private Item_Review_Trailer[] TrailerJsonStr(String TrailerJsonStr)
                throws JSONException {

            final String RESULTS = "results";
            final String NAME = "name";
            final String KEY = "key";

            JSONObject ReviewdataJson = new JSONObject(TrailerJsonStr);
            JSONArray ReviewdataArray = ReviewdataJson.getJSONArray(RESULTS);

            int numofresults = ReviewdataArray.length();

            Item_Review_Trailer[] resultStrs = new Item_Review_Trailer[numofresults];

            String name;
            String link;

            for (int i = 0; i < numofresults; i++) {


                JSONObject ReviewmovieData = ReviewdataArray.getJSONObject(i);

                name = ReviewmovieData.getString(NAME);
                link = ReviewmovieData.getString(KEY);
                link = "https://www.youtube.com/watch?v="+link;

                Item_Review_Trailer element = new Item_Review_Trailer("trailer",name,link);

                resultStrs[i] = element;
            }
            return resultStrs;

        }

        @Override
        protected Item_Review_Trailer[] doInBackground(String... strings) {


            if (strings.length == 0) {
                return null;
            }

            String id = strings[0];
            String api_key = "5c1d3fa899d823cdca9845d785e47b7d";

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String dataReviewJsonStr = null;

            try {
                //http://api.themoviedb.org/3/movie/131634/reviews?

                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + id + "/videos?";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, api_key)
                        .build();

                URL url_review = new URL(builtUri.toString());


                urlConnection = (HttpURLConnection) url_review.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                // Read the input stream into a String
                InputStream inputStream1 = urlConnection.getInputStream();

                StringBuffer buffer1 = new StringBuffer();
                if (inputStream1 == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream1));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer1.append(line + "\n");
                }

                if (buffer1.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                dataReviewJsonStr = buffer1.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return TrailerJsonStr(dataReviewJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Item_Review_Trailer[] result) {
            if (result != null && result.length > 0) {
                Adapter=new Item_Review_Trailer();
                for(Item_Review_Trailer Trailer_Adapter : result) {
                    Adapter.add(Trailer_Adapter);
                }

                NumOfTrailers = Adapter.size();
                Review_Async review_async = new Review_Async();
                review_async.execute(id);
            }
            // New data is back from the server.  Hooray!
        }

    }
}