package edu.uci.ics.fabflixmobile.ui.movielist;

import android.text.TextUtils;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Genre;
import edu.uci.ics.fabflixmobile.data.model.Movie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.uci.ics.fabflixmobile.data.model.Star;

import java.util.ArrayList;
import java.util.List;

public class MovieListViewAdapter extends ArrayAdapter<Movie> {
    private final ArrayList<Movie> movies;

    // View lookup cache
    private static class ViewHolder {

        TextView id;
        TextView title;
        TextView year;

        TextView actors;
        TextView genres;

        TextView director;
    }

    public MovieListViewAdapter(Context context, ArrayList<Movie> movies) {
        super(context, R.layout.movielist_row, movies);
        this.movies = movies;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the movie item for this position
        Movie movie = movies.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.movielist_row, parent, false);
            viewHolder.title = convertView.findViewById(R.id.titleTextView);
            viewHolder.year = convertView.findViewById(R.id.yearTextView);
            viewHolder.director = convertView.findViewById(R.id.directorTextView);
            viewHolder.actors = convertView.findViewById(R.id.actorsTextView);
            viewHolder.genres = convertView.findViewById(R.id.genresTextView);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.title.setText(movie.getName());
        viewHolder.year.setText(movie.getYear() + "");
        viewHolder.director.setText("Director: " + movie.getDirector());
        viewHolder.genres.setText("Genres: " + getGenresAsString(movie.getGenres()));
        viewHolder.actors.setText("Stars: " + getActorsAsString(movie.getStars()));



        // Return the completed view to render on screen
        return convertView;
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
        return TextUtils.join(", ", actorNames);
    }

}