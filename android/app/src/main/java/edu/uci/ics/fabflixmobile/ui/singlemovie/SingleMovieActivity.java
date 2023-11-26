package edu.uci.ics.fabflixmobile.ui.singlemovie;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.data.CustomJSONParser;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Genre;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.data.model.Star;
import edu.uci.ics.fabflixmobile.databinding.ActivitySinglemovieBinding;
import edu.uci.ics.fabflixmobile.ui.mainpage.MainPageActivity;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingleMovieActivity extends AppCompatActivity {

    public interface SingleMovieCallback {
        void onMoviesReceived(ArrayList<Movie> movies);
        void onError(String errorMessage);
    }

    /*
      In Android, localhost is the address of the device or the emulator.
      To connect to your machine, you need to use the below IP address
     */
//    private final String host = "10.0.2.2";
    private final String host = "13.52.42.148";

    private final String port = "8443";
    private final String domain = "cs122b-project4";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;

    TextView movieTitle;
    TextView movieYear;
    TextView movieDirector;
    TextView movieGenres;
    TextView movieStars;


    Movie movie;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySinglemovieBinding binding = ActivitySinglemovieBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());

        String movieId = getIntent().getStringExtra("movie_id");

        movieTitle = binding.movieTitle;
        movieYear = binding.movieYear;
        movieDirector = binding.directorNameSinglePage;
        movieGenres = binding.genresNames;
        movieStars = binding.starsNames;

        fetchMovie(movieId);
    }

    private void getMovieData(String movieId, SingleMovieCallback callback) {
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        // request type is POST
        final StringRequest searchRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/app/api/single-movie?id=" + movieId,
                response -> {
                    Log.d("search.response single movie", response);


                    ArrayList<Movie> movies = CustomJSONParser.parseJson(response);
                    callback.onMoviesReceived(movies);
                },
                error -> {
                    Log.d("search.error", error.toString());
                    callback.onError(error.toString());
                }) {
        };

        // important: queue.add is where the login request is actually sent
        queue.add(searchRequest);
    }

    private void fetchMovie(String movieId) {
        // Show loading state

        // Make API request with updated page
        getMovieData(movieId, new SingleMovieCallback() {
            @Override
            public void onMoviesReceived(ArrayList<Movie> movies) {
                movieTitle.setText(movies.get(0).getName());
                movieYear.setText(String.format("%s", movies.get(0).getYear()));
                movieDirector.setText(String.format("\n\t%s", movies.get(0).getDirector()));
                movieGenres.setText(String.format("\n\t%s", getGenresAsString(movies.get(0).getGenres())));
                movieStars.setText(String.format("\n\t%s", getActorsAsString(movies.get(0).getStars())));

            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getApplicationContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        // Customize the behavior of the back button
        // For example, you can navigate to a specific activity:
        Intent intent = new Intent(SingleMovieActivity.this, MainPageActivity.class);
        startActivity(intent);

        // Or perform any other action...

        // Don't forget to call super.onBackPressed() if you still want
        // the default behavior of finishing the current activity.
         super.onBackPressed();
    }

    private String getGenresAsString(List<Genre> genres) {
        // Convert the list of genre names to a comma-separated string
        List<String> genreNames = new ArrayList<>();
        for (Genre genre : genres) {
            genreNames.add(genre.getName());
        }
        return TextUtils.join(", ", genreNames);
    }

    private String getActorsAsString(List<Star> actors) {
        // Convert the list of actor names to a comma-separated string
        List<String> actorNames = new ArrayList<>();
        for (Star actor : actors) {
            actorNames.add(actor.getName());
        }
        return TextUtils.join("\n ", actorNames);
    }
}
