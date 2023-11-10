import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import classes.SessionParameters;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/movie-list"
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movie-list")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        //this.nameAttribute = new SessionAttribute<>(String.class, "name");
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    // Generates a SQL query String
    private String getQueryString(SessionParameters sessionParameters) {
        String query = "SELECT DISTINCT m.id, m.title AS title, m.year, m.director, r.rating AS rating " +
                "FROM movies m, ratings r ";

        if (sessionParameters.movieGenreId != null) {
            query += ", genres_in_movies gim " +
                    "WHERE m.id = gim.movieId AND m.id = r.movieId AND gim.genreID = "
                    + sessionParameters.movieGenreId + " ";

        } else if (sessionParameters.movieFirstChar != null) {
            query += " WHERE m.id = r.movieId AND title ";

            if (sessionParameters.movieFirstChar.equals("none")) {
                query += "NOT REGEXP '^[0-9a-zA-Z]' ";

            } else {
                query += "LIKE '" + sessionParameters.movieFirstChar + "%' ";
            }
            //query += "ORDER BY m.title ASC ";
        } else {
            boolean isFirstSearchParameter = true;

            if (sessionParameters.movieStar != null) {
                query += ", stars s, stars_in_movies sim " +
                        "WHERE m.id = sim.movieId AND s.id = sim.starId AND r.movieId = m.id ";
                query += "AND s.name LIKE '%" + sessionParameters.movieStar + "%' ";
                isFirstSearchParameter = false;
            } else {
                query += "WHERE m.id = r.movieId ";
            }
            if (sessionParameters.movieTitle != null) {
                query += "AND m.title LIKE '%" + sessionParameters.movieTitle + "%' ";
                isFirstSearchParameter = false;
            }
            if (sessionParameters.movieYear != null) {
                query +=  "AND m.year = '" + sessionParameters.movieYear + "' ";
                isFirstSearchParameter = false;
            }
            if (sessionParameters.movieDirector != null) {
                query += "AND m.director LIKE '%" + sessionParameters.movieDirector + "%' ";
            }
        }

        if (sessionParameters.sortOrderFirst == null) {
            query += "ORDER BY rating DESC ";
        } else {
            query += "ORDER BY " + sessionParameters.sortOrderFirst + " " + sessionParameters.sortDirectionFirst + ", ";
            query += sessionParameters.sortOrderSecond + " " + sessionParameters.sortDirectionSecond + " ";
        }

        if (sessionParameters.numResultsPerPage == 25) {
            query += "LIMIT 25 ";
            if (sessionParameters.offset == 0) {
                query += ";";
            } else {
                query += "OFFSET " + Integer.toString(25 * sessionParameters.offset + 1) + ";";
            }
        } else {
            query += "LIMIT " + Integer.toString(sessionParameters.numResultsPerPage) + " ";
            if (sessionParameters.offset == 0) {
                query += ";";
            } else {
                query += "OFFSET " + Integer.toString(
                        sessionParameters.numResultsPerPage * sessionParameters.offset + 1) + ";";
            }
        }
        return query;
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        SessionParameters sessionParameters = new SessionParameters(request);

        request.getServletContext().log(sessionParameters.toString());

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            String query = getQueryString(sessionParameters);
            request.getServletContext().log("\n\nSQL query: " + query);

            // Perform the query
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet  = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of resultSet
            while (resultSet.next()) {
                // save movie_id for further mysql queries in stars and genres
                String movie_id = resultSet.getString("id");

                // Create a JsonObject based on the data we retrieve from resultSet
                JsonObject jsonObject = new JsonObject();

                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", resultSet.getString("title"));
                jsonObject.addProperty("year", resultSet.getString("year"));
                jsonObject.addProperty("director", resultSet.getString("director"));
                jsonObject.addProperty("rating", resultSet.getString("rating"));


                String genreQuery = "SELECT g.name, g.id " +
                        "FROM genres g, genres_in_movies gim " +
                        "WHERE g.id = gim.genreID AND gim.movieId = ? " +
                        "ORDER BY g.name " +
                        "LIMIT 3;";
                PreparedStatement prepStatement = conn.prepareStatement(genreQuery);
                prepStatement.setString(1, movie_id);
                ResultSet genreResultSet = prepStatement.executeQuery();

                JsonObject genreObject = new JsonObject();

                int count = 0;
                while (genreResultSet.next()) {
                    JsonObject singleGenreObject = new JsonObject();
                    singleGenreObject.addProperty("id", genreResultSet.getString("id"));
                    singleGenreObject.addProperty("name", genreResultSet.getString("name"));
                    genreObject.add(Integer.toString(count), singleGenreObject);
                    count += 1;
                }

                // Getting the stars for a particular movie
                String starsQuery = "SELECT S.id, S.name " +
                        "FROM stars S, stars_in_movies SiM " +
                        "WHERE S.id = SiM.starId AND SiM.movieId = ? " +
                        "LIMIT 3;";
                prepStatement = conn.prepareStatement(starsQuery);
                prepStatement.setString(1, movie_id);
                ResultSet starsResultSet = prepStatement.executeQuery();
                JsonObject starObject = new JsonObject();

                // Create another jsonObject holding all stars and ids
                count = 0;
                while (starsResultSet.next()) {
                    JsonObject singleStarObject = new JsonObject();
                    singleStarObject.addProperty("id", starsResultSet.getString("id"));
                    singleStarObject.addProperty("name", starsResultSet.getString("name"));
                    starObject.add(Integer.toString(count), singleStarObject);
                    count += 1;
                }

                jsonObject.add("stars", starObject);
                jsonObject.add("genres", genreObject);
                jsonArray.add(jsonObject);

                prepStatement.close();
                if (resultSet.isLast()) {
                    starsResultSet.close();
                    genreResultSet.close();
                }
            }
            resultSet.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources
    }
}
