package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.databinding.ActivityMainpageBinding;
import edu.uci.ics.fabflixmobile.databinding.ActivityMovielistBinding;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import edu.uci.ics.fabflixmobile.ui.mainpage.MainPageActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieListActivity extends AppCompatActivity {

    private final String host = "10.0.2.2";
    private final String port = "8443";
    private final String domain = "cs122b_project3_war";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMovielistBinding binding = ActivityMovielistBinding.inflate(getLayoutInflater());

//        setContentView(R.layout.activity_movielist);
        setContentView(binding.getRoot());
        // TODO: this should be retrieved from the backend server
        final Button backButton = binding.backButton;
        backButton.setOnClickListener(view -> back());

        final ArrayList<Movie> movies = getMovies();
//        movies.add(new Movie("The Terminal", (short) 2004));
//        movies.add(new Movie("The Final Season", (short) 2007));
//        movies.add(new Movie(getIntent().getStringExtra("movie_title"), (short) 2023));
        MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Movie movie = movies.get(position);
            @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getYear());
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("SetTextI18n")
    public void back() {
        finish();
        // initialize the activity(page)/destination
        Intent SearchPage = new Intent(MovieListActivity.this, MainPageActivity.class);
        // activate the list page.
        startActivity(SearchPage);
    }

    @SuppressLint("SetTextI18n")
    public ArrayList<Movie> getMovies() {
        String movie_title = getIntent().getStringExtra("movie_title");
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        System.out.println("HERE");
        // request type is POST
        final StringRequest searchRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/app/api/movie-list",
                response -> {
                    // TODO: should parse the json response to redirect to appropriate functions
                    //  upon different response value.
                    Log.d("search.response", response);

                    try {
                        JSONArray jsonObject = new JSONArray(response);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    // error
                    Log.d("search.error", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                // POST request form data
                final Map<String, String> params = new HashMap<>();
                params.put("movie_title", movie_title);
                return params;
            }
        };
        // important: queue.add is where the login request is actually sent
        queue.add(searchRequest);

        final ArrayList<Movie> movies = new ArrayList<>();
        movies.add(new Movie("The Terminal", (short) 2004));
        movies.add(new Movie("The Final Season", (short) 2007));
        movies.add(new Movie(getIntent().getStringExtra("movie_title"), (short) 2023));
        return movies;
    }
}