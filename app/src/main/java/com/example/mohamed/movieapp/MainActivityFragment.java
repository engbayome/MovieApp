package com.example.mohamed.movieapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {


    private Item[] dataAdapter ;
    GridView gridView;
    DatabaseHelper Database;
    private callback listener;
    String FirstJSON;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);

    }

    public interface callback{
        public void onItemSelectedListner(String json);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh_settings) {
            updateData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // Get a reference to the ListView, and attach this adapter to it.
        gridView = (GridView) rootView.findViewById(R.id.main_grid_view);

        updateData();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               updateDetail(dataAdapter[i].json);
            }
        });
        return rootView;
    }


    private void saveFirstMovie(String json){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FirstMovie",getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("json",json);
        editor.apply();
    }
    @Override
    public void onAttach(Context activity){
        super.onAttach(activity);
        listener = (callback) getActivity();
    }

    public void updateDetail(String json){
        listener.onItemSelectedListner(json);
    }

    private void updateData() {
        GetData DataTask = new GetData();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_most));
        if (location.equals("favorites")){
            Database = new DatabaseHelper(this.getContext());
            dataAdapter = Database.getData();
            CustomArrayAdapter adapter =new CustomArrayAdapter(getContext() , R.layout.item , dataAdapter);
            gridView.setAdapter(adapter);
            FirstJSON = dataAdapter[0].json;
            saveFirstMovie(FirstJSON);
        }else{
            DataTask.execute(location);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateData();
    }


    public class GetData extends AsyncTask<String, Void, Item[]> {

        private final String LOG_TAG = GetData.class.getSimpleName();


        private Item[] getDataFromJson(String JsonStr)
                throws JSONException{

            // These are the names of the JSON objects that need to be extracted.
            final String RESULTS = "results";
            final String IMG_PATH = "poster_path";
            final String TITLE = "title";
            final String ID = "id";

            JSONObject dataJson = new JSONObject(JsonStr);
            JSONArray dataArray = dataJson.getJSONArray(RESULTS);

            int numofresults = dataArray.length();

            Item[] resultStrs = new Item[numofresults];

            String img_path;
            String title;
            String id;
            String json;

            for(int i = 0; i < numofresults; i++) {


                JSONObject movieData = dataArray.getJSONObject(i);

                json = movieData.toString();
                img_path = movieData.getString(IMG_PATH);
                img_path ="http://image.tmdb.org/t/p/w185"+img_path;
                title = movieData.getString(TITLE);
                id = movieData.getString(ID);

                Item element = new Item(id,img_path,title,json);

                resultStrs[i] = element;
            }
            FirstJSON = resultStrs[0].json;
            saveFirstMovie(FirstJSON);
            return resultStrs;
        }

        @Override
        protected Item[] doInBackground(String... strings) {


            if (strings.length == 0) {
                return null;
            }


            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String dataJsonStr = null;
            String parm = strings[0];
            String api_key = "5c1d3fa899d823cdca9845d785e47b7d";


            try {

                final String BASE_URL =
                        "https://api.themoviedb.org/3/discover/movie?";
                final String QUERY_PARAM = "sort_by";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, parm)
                        .appendQueryParameter(APPID_PARAM, api_key)
                        .build();

                URL url = new URL(builtUri.toString());



                // Create the request to OpenWeatherMap, and open the connection
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
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                dataJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
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
         protected void onPostExecute(Item[] result) {
            if (result != null) {
                dataAdapter = result;
                FirstJSON = dataAdapter[0].json;
                CustomArrayAdapter adapter =new CustomArrayAdapter(getContext() , R.layout.item , dataAdapter);
                gridView.setAdapter(adapter);
            }
        }

    }

}
