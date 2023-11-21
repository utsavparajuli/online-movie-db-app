package edu.uci.ics.fabflixmobile.data;

import edu.uci.ics.fabflixmobile.data.model.Genre;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.data.model.Star;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CustomJSONParser {
    public static ArrayList<Movie> parseJson(String jsonResponse) {
        ArrayList<Movie> movieList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonMovie = jsonArray.getJSONObject(i);

                String movieId = jsonMovie.getString("movie_id");
                String movieTitle;
                if(jsonMovie.has("movie_title")) {
                    movieTitle = jsonMovie.getString("movie_title");
                }
                else {
                    movieTitle = jsonMovie.getString("title");
                }
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
