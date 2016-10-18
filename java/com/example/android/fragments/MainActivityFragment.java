package com.example.android.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.data.Movie;
import com.example.android.movies.R;
import com.example.android.adapter.RecyclerAdapter;

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
import java.util.Collections;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private View rootView;
    //Recycler View
    private RecyclerAdapter recAdapter;
    private RecyclerView mRecyclerView;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    // Decision Variables
    private static int page = 1;
    private static String category = "popular";
    // DATA Variables
    private static ArrayList<Movie> movies = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Save RecyclerView State
        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Restore RecyclerView state
        if (mBundleRecyclerViewState != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movies_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_topRated) {

            resetPage(this.rootView);
            topRatedMovies();
            recAdapter.notifyDataSetChanged();

            return true;
        }else if (id == R.id.action_popular){

            resetPage(this.rootView);
            popularMovies();
            recAdapter.notifyDataSetChanged();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        this.rootView = rootView;

        // Checking Internet Connection
        if(!isNetworkAvailable()){
            Toast.makeText(getActivity(),"No Internet Connection",
                    Toast.LENGTH_LONG).show();
        }

        // Recycler View Initialization
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.movies_grid);
        recAdapter = new RecyclerAdapter(getContext(), movies);
        mRecyclerView.setAdapter(recAdapter);

        // Assigning Layout Manager for Recycler View
        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }
        else{
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        }

        // Displaying initial set of movies
        initMovies();
        setPageText(rootView);
        for(Movie m : movies){
            Log.d("Review: ", m.getTitle());
        }

        // Next | Previous Button Handling
        Button buttonPrev = (Button) rootView.findViewById(R.id.prev_button);
        if (page == 1){
            buttonPrev.setVisibility(View.GONE);
        }else{
            buttonPrev.setVisibility(View.VISIBLE);
        }
        buttonPrev.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                prevPage();
            }
        });

        Button buttonNext = (Button) rootView.findViewById(R.id.next_button);
        buttonNext.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                nextPage();
            }
        });

        return rootView;
    }

    private void setPageText(View root){
        TextView textView = (TextView) root.findViewById(R.id.page_number);
        textView.setText("Page " + page);
    }
    private void nextPage(){
        page++;
        GetMovies getNextMovies = new GetMovies();
        setPageText(rootView);
        getNextMovies.execute("https://api.themoviedb.org/3/movie/"+ category +"?api_key=33be9732d89e6e88a6922c96c52332ef&language=en-US&page="+page);
        Button buttonPrev = (Button) rootView.findViewById(R.id.prev_button);
        if (page == 1){
            buttonPrev.setVisibility(View.GONE);
        }else{
            buttonPrev.setVisibility(View.VISIBLE);
        }
    }
    private void prevPage(){
        page--;
        GetMovies getPrevMovies = new GetMovies();
        setPageText(rootView);
        getPrevMovies.execute("https://api.themoviedb.org/3/movie/"+ category +"?api_key=33be9732d89e6e88a6922c96c52332ef&language=en-US&page="+page);
        Button buttonPrev = (Button) rootView.findViewById(R.id.prev_button);
        if (page == 1){
            buttonPrev.setVisibility(View.GONE);
        }else{
            buttonPrev.setVisibility(View.VISIBLE);
        }
    }
    private void resetPage(View root){
        page = 1;
        setPageText(root);
        Button buttonPrev = (Button) root.findViewById(R.id.prev_button);
        buttonPrev.setVisibility(View.GONE);
    }
    private void topRatedMovies(){
        category = "top_rated";
        getActivity().setTitle("Top Rated Movies");
        GetMovies getMovies = new GetMovies();
        getMovies.execute("https://api.themoviedb.org/3/movie/"+ category +"?api_key=33be9732d89e6e88a6922c96c52332ef&language=en-US&page="+page);
    }
    private void popularMovies(){
        category = "popular";
        getActivity().setTitle("Popular Movies");
        GetMovies getMovies = new GetMovies();
        getMovies.execute("https://api.themoviedb.org/3/movie/"+ category +"?api_key=33be9732d89e6e88a6922c96c52332ef&language=en-US&page="+page);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void initMovies(){
        if(category.equals("popular")){
            popularMovies();
        }else{
            topRatedMovies();
        }
    }

    public class GetMovies extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = GetMovies.class.getSimpleName();

        private Movie[] getMovieDataFromJson(String jsonStr) throws JSONException {

            final String OWM_RESULTS = "results";
            final String OWM_POSTERPATH = "poster_path";
            final String OWM_ADULT = "adult";
            final String OWM_OVERVIEW = "overview";
            final String OWM_TITLE = "title";
            final String OWM_RELEASE = "release_date";
            final String OWM_ID = "id";
            final String OWM_USERVOTE = "vote_average";

            JSONObject json = new JSONObject(jsonStr);
            JSONArray movieArray = json.getJSONArray(OWM_RESULTS);

            Movie[] resultMovs = new Movie[movieArray.length()];
            for (int i = 0; i < movieArray.length(); i++) {
                String poster;
                Boolean adult;
                String overview;
                String title;
                String release;
                String id;
                String userVote;

                JSONObject movie = movieArray.getJSONObject(i);

                poster = movie.getString(OWM_POSTERPATH);
                adult = movie.getBoolean(OWM_ADULT);
                overview = movie.getString(OWM_OVERVIEW);
                title = movie.getString(OWM_TITLE);
                release = movie.getString(OWM_RELEASE);
                id = movie.getString(OWM_ID);
                userVote = movie.getString(OWM_USERVOTE);

                resultMovs[i] = new Movie(poster, title, adult, overview, release, id, userVote);
            }
            for (Movie m : resultMovs) {
                Log.v(LOG_TAG, "Movie entry: " + m.getTitle());
            }
            return resultMovs;

        }

        @Override
        protected Movie[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonStr = null;

            try {
                Log.d("URL", params[0]);
                URL url = new URL(params[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                movieJsonStr = buffer.toString();

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
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            if (result != null) {
                movies.clear();
                Collections.addAll(movies, result);
                recAdapter.notifyDataSetChanged();
            }
        }
    }
}

