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
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
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

            // Construct a query with parameter represented by "?"
//            String query = "SELECT GROUP_CONCAT(DISTINCT G.name SEPARATOR ', ') AS genres" +
//                    "from movies as m " +
//                    "where m.id = ?";

            String query = "SELECT M.id, M.title, M.year, M.director, GROUP_CONCAT(DISTINCT G.name SEPARATOR ', ') AS genres, R.rating " +
                    "FROM movies M, ratings R, genres G, genres_in_movies GiM " +
                    "WHERE M.id =? and R.movieId = M.id AND GiM.genreId = G.id AND GiM.movieId = M.id " +
                    "GROUP BY M.id, R.rating";

//
            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {

//                String starId = rs.getString("starId");
//                String starName = rs.getString("name");
//                String starDob = rs.getString("birthYear");

                String movieId = rs.getString("id");
                String movieTitle = rs.getString("title");
                String movieYear = rs.getString("year");
                String movieDirector = rs.getString("director");
                String genres = rs.getString("genres");
                String rating = rs.getString("rating");


                Statement statementStars = conn.createStatement();


                String query2 =  "SELECT s.name, s.id " +
                        "FROM   stars_in_movies as sm, stars as s " +
                        "WHERE  sm.movieID = '" + movieId + "' AND sm.starId = s.id";


                ResultSet rs2 = statementStars.executeQuery(query2);


//                System.out.println("Here 2");

                int counter = 0;
                JsonArray actors = new JsonArray();


                while (rs2.next()) {
                    JsonObject jsonObjectActors = new JsonObject();
                    jsonObjectActors.addProperty("actor_id", rs2.getString("id"));
                    jsonObjectActors.addProperty("actor_name", rs2.getString("name"));

                    actors.add(jsonObjectActors);

                }

                rs2.close();
                statementStars.close();

                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
//                jsonObject.addProperty("star_id", starId);
//                jsonObject.addProperty("star_name", starName);
//                jsonObject.addProperty("star_dob", starDob);
                jsonObject.addProperty("movie_id", movieId);
                jsonObject.addProperty("movie_title", movieTitle);
                jsonObject.addProperty("movie_year", movieYear);
                jsonObject.addProperty("movie_director", movieDirector);
                jsonObject.addProperty("movie_genres", genres);
                jsonObject.addProperty("movie_rating", rating);
                

                jsonArray.add(jsonObject);
                jsonArray.addAll(actors);
            }
            rs.close();
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