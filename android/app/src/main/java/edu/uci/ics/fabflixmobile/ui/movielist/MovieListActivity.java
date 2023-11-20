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
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Genre;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.data.model.Star;
import edu.uci.ics.fabflixmobile.databinding.ActivityMainpageBinding;
import edu.uci.ics.fabflixmobile.databinding.ActivityMovielistBinding;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import edu.uci.ics.fabflixmobile.ui.mainpage.MainPageActivity;
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

    private final String host = "10.0.2.2";
    private final String port = "8443";
    private final String domain = "cs122b_project3_war";
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

//        getMovies(movie_title, currentPage, new MovieCallback() {
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
                Request.Method.POST,
                baseURL + "/app/api/movie-list",
                response -> {
                    Log.d("search.response", response);

                    ArrayList<Movie> movies = MovieParser.parseJson(response);
                    callback.onMoviesReceived(movies);
                },
                error -> {
                    Log.d("search.error", error.toString());
                    callback.onError(error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                // POST request form data
                final Map<String, String> params = new HashMap<>();
                params.put("movie_title", movieTitle);
                params.put("offset", String.valueOf(currentPage - 1));
                params.put("num_results", String.valueOf(PAGE_SIZE));

                return params;
            }
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




    public static class MovieParser {
        public static ArrayList<Movie> parseJson(String jsonResponse) {
            ArrayList<Movie> movieList = new ArrayList<>();

            try {
                JSONArray jsonArray = new JSONArray(jsonResponse);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonMovie = jsonArray.getJSONObject(i);

                    String movieId = jsonMovie.getString("movie_id");
                    String movieTitle = jsonMovie.getString("movie_title");
                    String year = jsonMovie.getString("year");
                    String director = jsonMovie.getString("director");
                    String rating = jsonMovie.getString("rating");

                    // Parse stars array
                    JSONObject starsArray = jsonMovie.getJSONObject("stars");
                    List<Star> stars = parseStars(starsArray);

                    // Parse genres array
                    JSONObject genresArray = jsonMovie.getJSONObject("genres");
                    List<Genre> genres = parseGenres(genresArray);

                    // Create Movie object and add to the list
                    Movie movie = new Movie(movieId, movieTitle, Short.parseShort(year), director, rating, genres, stars);
                    movieList.add(movie);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return movieList;
        }

        private static List<Star> parseStars(JSONObject starsObject) throws JSONException {
            List<Star> stars = new ArrayList<>();

            for (int j = 0; j < starsObject.length(); j++) {
                JSONObject jsonStar = starsObject.getJSONObject(String.valueOf(j));
                String starId = jsonStar.getString("id");
                String starName = jsonStar.getString("name");
                stars.add(new Star(starId, starName));
            }

            return stars;
        }

        private static List<Genre> parseGenres(JSONObject genresObject) throws JSONException {
            List<Genre> genres = new ArrayList<>();

            for (int k = 0; k < genresObject.length(); k++) {
                JSONObject jsonGenre = genresObject.getJSONObject(String.valueOf(k));
                String genreId = jsonGenre.getString("id");
                String genreName = jsonGenre.getString("name");
                genres.add(new Genre(genreId, genreName));
            }

            return genres;
        }
    }
}