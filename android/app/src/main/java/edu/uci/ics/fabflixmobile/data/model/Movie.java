package edu.uci.ics.fabflixmobile.data.model;

import java.util.List;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Movie {
    private final String id;
    private final String name;
    private final short year;

    private final String director;
    private String rating;

    private List<Genre> genres;

    private List<Star> stars;

    public Movie(String id, String name, short year, String director, String rating, List<Genre> genres, List<Star> stars) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.director = director;
        this.rating = rating;
        this.genres = genres;
        this.stars = stars;
    }

    public String getId() {
        return id;
    }

    public String getDirector() {
        return director;
    }

    public String getRating() {
        return rating;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public List<Star> getStars() {
        return stars;
    }

    public String getName() {
        return name;
    }

    public short getYear() {
        return year;
    }
}