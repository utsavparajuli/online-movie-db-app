package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.databinding.ActivityMainpageBinding;
import edu.uci.ics.fabflixmobile.databinding.ActivityMovielistBinding;
import edu.uci.ics.fabflixmobile.ui.mainpage.MainPageActivity;

import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMovielistBinding binding = ActivityMovielistBinding.inflate(getLayoutInflater());

//        setContentView(R.layout.activity_movielist);
        setContentView(binding.getRoot());
        // TODO: this should be retrieved from the backend server
        final Button backButton = binding.backButton;
        backButton.setOnClickListener(view -> back());

        final ArrayList<Movie> movies = new ArrayList<>();
        movies.add(new Movie("The Terminal", (short) 2004));
        movies.add(new Movie("The Final Season", (short) 2007));
        movies.add(new Movie(getIntent().getStringExtra("movie_title"), (short) 2023));
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
}