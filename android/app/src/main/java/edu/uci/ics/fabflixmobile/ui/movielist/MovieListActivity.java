package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.CustomJSONParser;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Genre;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.data.model.Star;
import edu.uci.ics.fabflixmobile.databinding.ActivityMainpageBinding;
import edu.uci.ics.fabflixmobile.databinding.ActivityMovielistBinding;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import edu.uci.ics.fabflixmobile.ui.mainpage.MainPageActivity;
import edu.uci.ics.fabflixmobile.ui.singlemovie.SingleMovieActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieListActivity extends AppCompatActivity {

    public interface MovieCallback {
        void onMoviesReceived(ArrayList<Movie> movies);
        void onError(String errorMessage);
    }

    private int currentPage;
    private static final int PAGE_SIZE = 10;

//    private final String host = "10.0.2.2";
    private final String host = "13.52.42.148";

    private final String port = "8443";
    private final String domain = "cs122b-project4";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;

    private TextView pageNumber;

    private Button prevButton;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMovielistBinding binding = ActivityMovielistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentPage = 1;
        pageNumber = binding.pageNumber;

        final Button backButton = binding.backButton;
        prevButton = binding.prevButton;
        nextButton = binding.nextButton;

        backButton.setOnClickListener(view -> back());

        prevButton.setOnClickListener(v -> {
            currentPage--;
            fetchMovies(currentPage);
        });

        nextButton.setOnClickListener(v -> {
            currentPage++;
            fetchMovies(currentPage);
        });



        String movie_title = getIntent().getStringExtra("movie_title");


        fetchMovies(currentPage);

//        getMovies(movie_title, currentPage, new SingleMovieCallback() {
//            @Override
//            public void onMoviesReceived(ArrayList<Movie> movies) {
//                MovieListViewAdapter adapter = new MovieListViewAdapter(MovieListActivity.this, movies);
//                ListView listView = findViewById(R.id.list);
//                listView.setAdapter(adapter);
//                listView.setOnItemClickListener((parent, view, position, id) -> {
//                    Movie movie = movies.get(position);
//                    @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getYear());
//                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//                });
//            }
//
//            @Override
//            public void onError(String errorMessage) {
//                // Handle the error, e.g., show a toast
//                Toast.makeText(getApplicationContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        // Customize the behavior of the back button
        // For example, you can navigate to a specific activity:
        Intent intent = new Intent(MovieListActivity.this, MainPageActivity.class);
        startActivity(intent);

        // Or perform any other action...

        // Don't forget to call super.onBackPressed() if you still want
        // the default behavior of finishing the current activity.
        super.onBackPressed();
    }


    @SuppressLint("SetTextI18n")
    public void back() {
        finish();
        // initialize the activity(page)/destination
        Intent SearchPage = new Intent(MovieListActivity.this, MainPageActivity.class);
        // activate the list page.
        startActivity(SearchPage);
    }

    public void getMovies(String movieTitle, int currentPage, MovieCallback callback) {
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        // request type is POST
        final StringRequest searchRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/app/api/movie-list?movie_title=" + movieTitle + "&offset=" + (currentPage - 1)
                + "&num_results=" + PAGE_SIZE,
                response -> {
                    Log.d("search.response", response);

                    ArrayList<Movie> movies = CustomJSONParser.parseJson(response);
                    callback.onMoviesReceived(movies);
                },
                error -> {
                    Log.d("search.error", error.toString());
                    callback.onError(error.toString());
                }) {
//            @Override
//            protected Map<String, String> getParams() {
//                // POST request form data
//                final Map<String, String> params = new HashMap<>();
//                params.put("movie_title", movieTitle);
//                params.put("offset", String.valueOf(currentPage - 1));
//                params.put("num_results", String.valueOf(PAGE_SIZE));
//
//                return params;
//            }
        };

        // important: queue.add is where the login request is actually sent
        queue.add(searchRequest);
    }

    private void fetchMovies(int page) {
        // Show loading state

        String movie_title = getIntent().getStringExtra("movie_title");

        // Make API request with updated page
        getMovies(movie_title, page, new MovieCallback() {
            @Override
            public void onMoviesReceived(ArrayList<Movie> movies) {
//                hideLoading();
                MovieListViewAdapter adapter = new MovieListViewAdapter(MovieListActivity.this, movies);
                ListView listView = findViewById(R.id.list);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener((parent, view, position, id) -> {
                    Movie movie = movies.get(position);
                    @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getYear());
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                    // initialize the activity(page)/destination
                    Intent MovieListPage = new Intent(MovieListActivity.this, SingleMovieActivity.class);
                    MovieListPage.putExtra("movie_id", movie.getId());
                    // activate the list page.
                    startActivity(MovieListPage);

                });
                pageNumber.setText(String.valueOf(currentPage));

                prevButton.setEnabled(currentPage > 1);
                nextButton.setEnabled(movies.size() >= 10);
            }

            @Override
            public void onError(String errorMessage) {
                // hideLoading();
                // Handle the error, e.g., show a toast
                Toast.makeText(getApplicationContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}