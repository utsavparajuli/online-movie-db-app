package classes;

import jakarta.servlet.http.HttpServletRequest;

public class SessionParameters {
    public String movieGenreId;
    public String movieFirstChar;
    public String movieTitle;
    public String movieYear;
    public String movieDirector;
    public String movieStar;
    public String sortOrderFirst;
    public String sortOrderSecond;
    public String sortDirectionFirst;
    public String sortDirectionSecond;
    public Integer offset;
    public Integer numResultsPerPage;

    public SessionParameters(HttpServletRequest request) {
        movieGenreId = request.getParameter("genre_id");
        movieFirstChar = request.getParameter("alphabet_id");
        movieTitle = request.getParameter("movie_title");
        movieYear = request.getParameter("movie_year");
        movieDirector = request.getParameter("movie_director");
        movieStar = request.getParameter("movie_star");
        sortOrderFirst = request.getParameter("first_sort");
        sortOrderSecond = request.getParameter("second_sort");
        sortDirectionFirst = request.getParameter("first_dir");
        sortDirectionSecond = request.getParameter("second_dir");

        if (request.getParameter("offset") == null) {
            offset = 0;
        } else {
            offset = Integer.parseInt(request.getParameter("offset"));
        }

        if (request.getParameter("num_results") == null) {
            numResultsPerPage = 25;
        } else {
            numResultsPerPage = Integer.parseInt(request.getParameter("num_results"));
        }

    }

    public void updateParameters(HttpServletRequest request) {
        offset = Integer.parseInt(request.getParameter("offset"));
        sortOrderFirst = request.getParameter("first_sort");
        sortOrderSecond = request.getParameter("second_sort");
        sortDirectionFirst = request.getParameter("first_dir");
        sortDirectionSecond = request.getParameter("second_dir");
    }

    @Override
    public String toString() {
        return "\ngenre_id: " + this.movieGenreId + "\n" +
                "alphabet_id: " + this.movieFirstChar + "\n" +
                "movie_title: " + this.movieTitle + "\n" +
                "movie_year: " + this.movieYear + "\n" +
                "movie_director: " + this.movieDirector + "\n" +
                "movie_star: " + this.movieStar + "\n" +
                "first_sort: " + this.sortOrderFirst + "\n" +
                "second_sort: " + this.sortOrderSecond + "\n" +
                "first_dir: " + this.sortDirectionFirst + "\n" +
                "second_dir: " + this.sortDirectionSecond + "\n" +
                "offset: " + this.offset.toString() + "\n" +
                "num_results: " + this.numResultsPerPage.toString();
    }

}
