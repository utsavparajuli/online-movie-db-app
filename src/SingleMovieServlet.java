import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Declaring a WebServlet called SingleMovieServlet, which maps to url "/api/single-movie"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/app/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/slavemoviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        System.out.println(id);

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?". Gets the movie details based on the '?'
            String query = "SELECT m.id, m.title, m.year, m.director, r.rating " +
                    "FROM movies m, ratings r " +
                    "WHERE r.movieId = m.id AND m.id = ? " +
                    "GROUP BY m.id, r.rating " +
                    "ORDER BY r.rating DESC;";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();

            // Return array
            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of resultSet
            while (resultSet.next()) {
                // Create a JsonObject based on the data we retrieve from resultSet
                JsonObject jsonObject = new JsonObject();

                String movieId = resultSet.getString("id");

                jsonObject.addProperty("movie_id", movieId);
                jsonObject.addProperty("title", resultSet.getString("title"));
                jsonObject.addProperty("year", resultSet.getString("year"));
                jsonObject.addProperty("director", resultSet.getString("director"));
                jsonObject.addProperty("rating", resultSet.getString("rating"));

                String starsQuery = "SELECT s.id, s.name, COUNT(DISTINCT sim.movieId) as movieCount " +
                        "FROM stars s, stars_in_movies sim " +
                        "WHERE s.id = sim.starId and s.id IN " +
                            "(SELECT s.id " +
                            "FROM stars s, stars_in_movies sim " +
                            "WHERE s.id = sim.starId AND sim.movieID = ?) " +
                        "GROUP BY s.id " +
                        "ORDER BY movieCount DESC, s.name;";

                PreparedStatement prepStatement = conn.prepareStatement(starsQuery);
                prepStatement.setString(1, movieId);
                ResultSet starsResultSet = prepStatement.executeQuery();
                JsonObject starObject = new JsonObject();

                // Create another jsonObject holding all stars and ids
                int count = 0;
                while (starsResultSet.next()) {
                    JsonObject singleStarObject = new JsonObject();
                    singleStarObject.addProperty("id", starsResultSet.getString("id"));
                    singleStarObject.addProperty("name", starsResultSet.getString("name"));
                    starObject.add(Integer.toString(count), singleStarObject);
                    count += 1;
                }

                jsonObject.addProperty("star_num", Integer.toString(count));

                String genreQuery = "SELECT g.name, g.id " +
                        "FROM genres g, genres_in_movies gim " +
                        "WHERE g.id = gim.genreID AND gim.movieId = ? " +
                        "ORDER BY g.name;";

                prepStatement = conn.prepareStatement(genreQuery);
                prepStatement.setString(1, movieId);
                ResultSet genreResultSet = prepStatement.executeQuery();
                JsonObject genreObject = new JsonObject();

                count = 0;
                while (genreResultSet.next()) {
                    JsonObject singleGenreObject = new JsonObject();
                    singleGenreObject.addProperty("id", genreResultSet.getString("id"));
                    singleGenreObject.addProperty("name", genreResultSet.getString("name"));
                    genreObject.add(Integer.toString(count), singleGenreObject);
                    count += 1;
                }

                jsonObject.addProperty("genre_num", Integer.toString(count));
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
