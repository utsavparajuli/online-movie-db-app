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


// Declaring a WebServlet called StarsServlet, which maps to url "/api/movielist"
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movielist")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            Statement statement = conn.createStatement();

            String query = "SELECT M.id, M.title, M.year, M.director, " +
                    "GROUP_CONCAT(DISTINCT G.name SEPARATOR ', ') AS genres, R.rating " +
                    "FROM movies M, ratings R, genres G, genres_in_movies GiM " +
                    "WHERE R.movieId = M.id AND M.id = GiM.movieId AND GiM.genreId = G.id " +
                    "GROUP BY M.id, R.rating " +
                    "ORDER BY R.rating DESC " +
                    "LIMIT 20;";

            // Perform the query
            ResultSet resultSet  = statement.executeQuery(query);

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
                jsonObject.addProperty("genres", resultSet.getString("genres"));
                jsonObject.addProperty("rating", resultSet.getString("rating"));

                String starsQuery = "SELECT S.id, S.name " +
                        "FROM stars S, stars_in_movies SiM " +
                        "WHERE S.id = SiM.starId AND SiM.movieId = ? " +
                        "LIMIT 3;";
                PreparedStatement starsStatement = conn.prepareStatement(starsQuery);
                starsStatement.setString(1, movie_id);

                ResultSet starsResultSet = starsStatement.executeQuery();

                StringBuilder starNames = new StringBuilder();
                StringBuilder starIds = new StringBuilder();

                while (starsResultSet.next()) {
                    starNames.append(starsResultSet.getString("name")).append(", ");
                    starIds.append(starsResultSet.getString("id")).append(", ");
                }
                String starNamesString = starNames.toString();
                String starIdsString = starIds.toString();
                starNamesString = starNamesString.replace(", $", "");
                starIdsString = starIdsString.replace(", $", "");


                jsonObject.addProperty("star_names", starNamesString);
                jsonObject.addProperty("star_ids", starIdsString);

                jsonArray.add(jsonObject);

                if (resultSet.isLast()) {
                    starsResultSet.close();
                }
            }

            resultSet .close();
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

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}
