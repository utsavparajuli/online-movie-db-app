package dto;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Film {
    private String id;
    private String title;
    private Year year;
    private String director;
    private double rating;
    private List<Genre> genres;

    public Film() {
        year = null;
        genres = new ArrayList<>();
    }

    public Film(String id) {
        this.id = id;
        year = null;
        genres = new ArrayList<>();
    }

    public Film(String id, String title, Year year, String director, double rating, List<Genre> genres) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.rating = rating;
        this.genres = genres;
    }

    public Film(String id, String title, Year year, String director, double rating) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.rating = rating;
        genres = new ArrayList<>();
    }

    public boolean validGenres() {
        for (Genre g:
             genres) {
            if(!g.isGenreNameValid()) {
                return false;
            }
        }

        return true;
    }

    public boolean hasError() {
        if(id == null || title == null || year == null || director == null) {
            return true;
        }
        else if (!id.matches("^[a-zA-Z0-9]+$")) {
            return true;
        }
        else if (!title.matches("^[a-zA-Z0-9:',!?.()\\-\\s]+$")) {
            return true;
        }
        else if(!director.matches("^[a-zA-Z.'\\-\\s]+$")) {
            return true;
        }
        else if (!validGenres()) {
            return true;
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Year getYear() {
        return year;
    }

    public void setYear(Year year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public void addGenre(Genre g) {
        this.genres.add(g);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return Objects.equals(getId(), film.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return String.format("Film " +
                "\t%s = %-10s" + // Left-justify id with a width of 10
                "\t%s = %-50s" + // Left-justify title with a width of 10
                "\t%s = %-8s" +   // Left-justify year with a width of 10
                "\t%s = %-20s" + // Left-justify director with a width of 10
                "\t%s = %.2f" + // Left-justify rating with a width of 10
                "\t%s = %s" +   // Left-justify genres with a width of 10
                "", "id", id, "title", title, "year", year, "director", director, "rating", rating, "genres", genres.toString());

//        return "\n\t\tFilm{" +
//                "\n\t\t\tid='" + id + '\'' +
//                "\n\t\t\ttitle='" + title + '\'' +
//                "\n\t\t\tyear=" + year +
//                "\n\t\t\tdirector='" + director + '\'' +
//                "\n\t\t\trating=" + rating +
//                "\n\t\t\tgenres=" + genres.toString() +
//                "}\n";
    }
}
