import classes.SessionParameters;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/movie-list"
@WebServlet(name = "MovieListServlet", urlPatterns = "/app/api/movie-list")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    public static final String genreQuery = "CALL genre_query(?)";
    public static final  String starsQuery = "CALL stars_query(?)";
    private DataSource dataSource;
    FileWriter writer;
    File myfile;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/slavemoviedb");

            String xmlFilePath="/home/logs/tjMeasurement.txt";
            config.getServletContext().log(xmlFilePath);
            myfile = new File(xmlFilePath);
            myfile.createNewFile();
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getGenreQueryPart(SessionParameters sessionParameters) {
        return ", genres_in_movies gim " +
                String.format("WHERE m.id = gim.movieId AND m.id = r.movieId AND gim.genreID = %s ",
                        sessionParameters.movieGenreId);
    }

    private String getTitleStartsWithQueryPart(SessionParameters sessionParameters) {
        String query = " WHERE m.id = r.movieId AND title ";
        if (sessionParameters.movieFirstChar.equals("none")) {
            query += "NOT REGEXP '^[0-9a-zA-Z]' ";
        } else {
            query += "LIKE '" + sessionParameters.movieFirstChar + "%' ";
        }
        query += "ORDER BY m.title ASC ";
        return query;
    }

    private String getSearchQueryPart(SessionParameters sessionParameters) {
        boolean isFirstSearchParameter = true;
        StringBuilder query = new StringBuilder();
        if (sessionParameters.movieStar != null) {
            query.append(", stars s, stars_in_movies sim " +
                    "WHERE m.id = sim.movieId AND s.id = sim.starId AND r.movieId = m.id " +
                    "AND s.name LIKE '%").append(sessionParameters.movieStar).append("%' ");
            isFirstSearchParameter = false;
        } else {
            query.append("WHERE m.id = r.movieId ");
        }
        if (sessionParameters.movieTitle != null) {
            String[] titleArray = sessionParameters.movieTitle.split("[\\p{IsPunctuation}\\s]+");
            query.append("AND MATCH (m.title) AGAINST ('");
            for (String s : titleArray) {
                query.append("+").append(s).append("* ");
            }
            query.append("' IN BOOLEAN MODE) ");
            isFirstSearchParameter = false;
        }
        if (sessionParameters.movieYear != null) {
            query.append("AND m.year = ")
                    .append(sessionParameters.movieYear)
                    .append(" ");
            isFirstSearchParameter = false;
        }
        if (sessionParameters.movieDirector != null) {
            query.append("AND m.director LIKE '%")
                    .append(sessionParameters.movieDirector)
                    .append("%' ");
        }
        return query.toString();
    }

    private String getOrderQueryPart(SessionParameters sessionParameters) {
        String query = "";
        if (sessionParameters.sortOrderFirst == null) {
            query += "ORDER BY rating DESC ";
        } else {
            query += String.format("ORDER BY %s %s, %s %s ", sessionParameters.sortOrderFirst,
                    sessionParameters.sortDirectionFirst, sessionParameters.sortOrderSecond,
                    sessionParameters.sortDirectionSecond);
        }
        return query;
    }

    private String getLimitQueryPart(SessionParameters sessionParameters) {
        String query = "";
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

    private String getQueryString(SessionParameters sessionParameters) {
        StringBuilder query = new StringBuilder("SELECT DISTINCT m.id, m.title AS title, m.year, m.director, r.rating AS rating " +
                "FROM movies m, ratings r ");

        if (sessionParameters.movieGenreId != null) {
            query.append(getGenreQueryPart(sessionParameters));

        } else if (sessionParameters.movieFirstChar != null) {
            query.append(getTitleStartsWithQueryPart(sessionParameters));
        } else {
            query.append(getSearchQueryPart(sessionParameters));
        }
        if (sessionParameters.movieFirstChar == null) {
            query.append(getOrderQueryPart(sessionParameters));
        }
        query.append(getLimitQueryPart(sessionParameters));
        return query.toString();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        writer = new FileWriter(myfile, true);
        response.setContentType("application/json");
        SessionParameters sessionParameters = new SessionParameters(request);
        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {

            String query = getQueryString(sessionParameters);

            ResultSet resultSet;
            PreparedStatement statement;


            try (FileWriter localWriter = new FileWriter(myfile, true)) {
                // Perform the query
                statement = conn.prepareStatement(query);
                // Time an event in a program to nanosecond precision
                long tjStartTime = System.nanoTime();

                resultSet  = statement.executeQuery();

                long tjEndTime = System.nanoTime();
                long tjElapsedTime = tjEndTime - tjStartTime; // elapsed time in nanoseconds. Note: print the values in nanoseconds

                localWriter.write("TJ: " + tjElapsedTime + " ns\n");
            }


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
            writer.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources
    }
}
