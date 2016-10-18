package com.example.android.fragments;

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
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.activities.DetailActivity;
import com.example.android.adapter.ReviewAdapter;
import com.example.android.adapter.TrailerAdapter;
import com.example.android.data.Movie;
import com.example.android.data.Review;
import com.example.android.data.Trailer;
import com.example.android.movies.*;
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
import java.util.Collections;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    private Movie movie;

    private ImageView poster;
    private TextView release;
    private TextView userRating;
    private TextView overview;

    private ListView trailerList;
    private TrailerAdapter trailerAdapter;
    private ListView reviewList;
    private ReviewAdapter reviewAdapter;

    private ArrayList<Trailer> trailers = new ArrayList<>();
    private ArrayList<Review> reviews = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // Getting movie from intent
        if (intent != null) {
            movie = (com.example.android.data.Movie) intent.getSerializableExtra("Movie");
            getActivity().setTitle(movie.getTitle());
        }

        // Referencing Views
        poster = (ImageView) rootView.findViewById(R.id.poster_detail);
        release = (TextView) rootView.findViewById(R.id.release_date);
        userRating = (TextView) rootView.findViewById(R.id.user_rating);
        overview = (TextView) rootView.findViewById(R.id.overview);
        trailerList = (ListView) rootView.findViewById(R.id.listview_trailers);
        trailerAdapter = new TrailerAdapter(getContext(), trailers);
        reviewList = (ListView) rootView.findViewById(R.id.listview_reviews);
        reviewAdapter = new ReviewAdapter(getContext(), reviews);

        // Getting Trailers and Reviews
        GetTrailers getTrailers = new GetTrailers();
        getTrailers.execute("https://api.themoviedb.org/3/movie/" + movie.getId() + "/videos?api_key=33be9732d89e6e88a6922c96c52332ef");

        GetReviews getReviews = new GetReviews();
        getReviews.execute("https://api.themoviedb.org/3/movie/" + movie.getId() + "/reviews?api_key=33be9732d89e6e88a6922c96c52332ef");

        // Assigning Data to Views
        String url = "https://image.tmdb.org/t/p/w500" + movie.getPosterpath();
        Picasso.with(poster.getContext())
                .load(url)
                .into(poster);

        release.setText(movie.getReleaseDate());
        userRating.setText(movie.getUserVote());
        overview.setText(movie.getDescription());

        trailerList.setAdapter(trailerAdapter);
        reviewList.setAdapter(reviewAdapter);

        // Setting height of trailer list view dynamically
        int totalHeight = 0;
        for (int i = 0; i < trailerAdapter.getCount(); i++) {
            View listItem = trailerAdapter.getView(i, null, trailerList);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        trailerList.setMinimumHeight(totalHeight + (trailerList.getDividerHeight() * (trailerAdapter.getCount() - 1)));

        // Trailer List View Click Listeners - Implicit Intent
        trailerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Trailer trailer = (Trailer) trailerAdapter.getItem(position);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailer.getLink()));
                startActivity(intent);
            }
        });

        return rootView;
    }


    public class GetTrailers extends AsyncTask<String, Void, Trailer[]> {

        private final String LOG_TAG = DetailActivityFragment.GetTrailers.class.getSimpleName();

        private Trailer[] getTrailerDataFromJson(String jsonStr) throws JSONException {

            final String OWM_RESULTS = "results";
            final String OWM_KEY = "key";
            final String OWM_NAME = "name";

            JSONObject json = new JSONObject(jsonStr);
            JSONArray trailerArray = json.getJSONArray(OWM_RESULTS);

            Trailer[] resultTrailer = new Trailer[trailerArray.length()];
            for (int i = 0; i < trailerArray.length(); i++) {
                String key;
                String name;

                JSONObject trailer = trailerArray.getJSONObject(i);

                key = trailer.getString(OWM_KEY);
                name = trailer.getString(OWM_NAME);

                resultTrailer[i] = new Trailer(name, key);
            }
            for (Trailer t : resultTrailer) {
                Log.v(LOG_TAG, "Trailer entry: " + t.getName());
            }
            return resultTrailer;

        }

        @Override
        protected Trailer[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String trailerJsonStr = null;

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
                trailerJsonStr = buffer.toString();

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
                return getTrailerDataFromJson(trailerJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Trailer[] result) {
            if (result != null) {
                trailers.clear();
                Collections.addAll(trailers, result);
                //tailerAdapter.notifyDataSetChanged();
            }
        }
    }

    public class GetReviews extends AsyncTask<String, Void, Review[]> {

        private final String LOG_TAG = DetailActivityFragment.GetReviews.class.getSimpleName();

        private Review[] getReviewsDataFromJson(String jsonStr) throws JSONException {

            final String OWM_RESULTS = "results";
            final String OWM_AUTHOR = "author";
            final String OWM_CONTENT = "content";

            JSONObject json = new JSONObject(jsonStr);
            JSONArray reviewsArray = json.getJSONArray(OWM_RESULTS);

            Review[] resultReviews = new Review[reviewsArray.length()];
            for (int i = 0; i < reviewsArray.length(); i++) {
                String author;
                String content;

                JSONObject review = reviewsArray.getJSONObject(i);

                author = review.getString(OWM_AUTHOR);
                content = review.getString(OWM_CONTENT);

                resultReviews[i] = new Review(author, content);
            }
            for (Review r : resultReviews) {
                Log.v(LOG_TAG, "Review entry: " + r.getAuthor());
            }
            return resultReviews;

        }

        @Override
        protected Review[] doInBackground(String... params) {
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
                return getReviewsDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Review[] result) {
            if (result != null) {
                reviews.clear();
                Collections.addAll(reviews, result);
                //recAdapter.notifyDataSetChanged();
            }
        }
    }
}
