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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
    private String[] detailAdapter;
    private Item_Review_Trailer Adapter;
    ListView list_view;
    int NumOfTrailers;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);


        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(intent.EXTRA_TEXT)) {
            id = intent.getStringExtra(intent.EXTRA_TEXT);
        }

        list_view = (ListView) rootView.findViewById(R.id.listview);
        View header = inflater.inflate(R.layout.header, null ,false);
        list_view.addHeaderView(header);

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

        return rootView;
    }

    private void updateData() {
        GetDetailData DataTask = new GetDetailData();
        DataTask.execute(id);
        Trailer_Async trailer_async = new Trailer_Async();
        trailer_async.execute(id);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateData();
    }




    private class GetDetailData extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = GetDetailData.class.getSimpleName();


        private String[] getDataFromJson(String JsonStr)
                throws JSONException {


            // These are the names of the JSON objects that need to be extracted.
            final String TITLE = "title";
            final String IMG_PATH = "backdrop_path";
            final String RELEASE_DATA = "release_date";
            final String DURATION = "runtime";
            final String RATING = "vote_average";
            final String OVERVIEW = "overview";

            JSONObject movieData = new JSONObject(JsonStr);


            String title;
            String img_path;
            String release_date;
            String duration;
            String rating;
            String overview;


            title = movieData.getString(TITLE);
            img_path = movieData.getString(IMG_PATH);
            img_path = "http://image.tmdb.org/t/p/w185" + img_path;
            release_date = movieData.getString(RELEASE_DATA);
            duration = movieData.getString(DURATION);
            rating = movieData.getString(RATING);
            overview = movieData.getString(OVERVIEW);

            String[] resultStrs = {title, img_path, release_date, duration, rating, overview};

            return resultStrs;

        }

        @Override
        protected String[] doInBackground(String... strings) {


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
            String dataJsonStr = null;

            try {

                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + id + "?";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, api_key)
                        .build();

                URL url = new URL(builtUri.toString());


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();

                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                dataJsonStr = buffer.toString();
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
                return getDataFromJson(dataJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                detailAdapter = result;

                ImageView img = (ImageView) getView().findViewById(R.id.detail_image_view);
                Picasso.with(getContext()).load(detailAdapter[1]).into(img);
                TextView title = (TextView) getView().findViewById(R.id.detail_name_text);
                title.setText(detailAdapter[0]);
                TextView release_date = (TextView) getView().findViewById(R.id.release_date);
                release_date.setText(detailAdapter[2]);
                TextView duration = (TextView) getView().findViewById(R.id.duration);
                duration.setText(detailAdapter[3]);
                TextView user_rating = (TextView) getView().findViewById(R.id.user_rating);
                user_rating.setText(detailAdapter[4]);
                TextView overview = (TextView) getView().findViewById(R.id.overview);
                overview.setText(detailAdapter[5]);
            }
            // New data is back from the server.  Hooray!
        }

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