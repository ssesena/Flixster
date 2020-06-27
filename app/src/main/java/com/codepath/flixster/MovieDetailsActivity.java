package com.codepath.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.flixster.databinding.ActivityMovieDetailsBinding;
import com.codepath.flixster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import okhttp3.Headers;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String TAG = "MovieDetailsActivity";
    Context context = this;
    JSONObject video;
    Movie movie;
    String videoId;
    public static final String URL_PART_ONE = "https://api.themoviedb.org/3/movie/";
    public static final String URL_PART_TWO =  "/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed&language=en-US";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int radius = 50;
        int margin = 0;
        super.onCreate(savedInstanceState);
        ActivityMovieDetailsBinding binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));

        Log.d("MovieDetailsActivity", String.format("Showing details for '%s", movie.getTitle()));

        binding.tvTitle.setText(movie.getTitle());
        binding.tvOverview.setText(movie.getOverview());
        float voteAverage = movie.getVoteAverage().floatValue();
        binding.rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f: voteAverage);
        Glide.with(this).load(movie.getBiggerBackdropPath()).into(binding.ivBackdrop);

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(URL_PART_ONE+movie.getId()+URL_PART_TWO, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results" + results.toString());
                    if (results.length() > 0) {
                        video = results.getJSONObject(0);
                        videoId = video.getString("key");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }

        });
        binding.ivBackdrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (video != null) {
                    Intent intent = new Intent(context, MovieTrailerActivity.class);
                    intent.putExtra("videoId", videoId);
                    context.startActivity(intent);
                }
            }
        });
    }
}