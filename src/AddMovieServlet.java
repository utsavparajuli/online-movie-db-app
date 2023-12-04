import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "AddMovieServlet", urlPatterns = "/_dashboard/api/movie")
public class AddMovieServlet extends HttpServlet {
    public static final String addMovieCall = "CALL add_movie(?, ?, ?, ?, ?);";
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/mastermoviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject movieJsonObject = new JsonObject();
        PrintWriter out = response.getWriter();
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement addMovieStatement = conn.prepareStatement(addMovieCall);
            addMovieStatement.setString(1, request.getParameter("movie_title"));
            addMovieStatement.setString(2, request.getParameter("movie_director"));
            addMovieStatement.setString(3, request.getParameter("movie_year"));
            addMovieStatement.setString(4, request.getParameter("movie_genre"));
            addMovieStatement.setString(5, request.getParameter("movie_star"));
            ResultSet movieResultSet =  addMovieStatement.executeQuery();
            while(movieResultSet.next()) {
                String answer = movieResultSet.getString("answer");
                if (answer.equals("no update")) {
                    movieJsonObject.addProperty("update", "none");
                } else {
                    String[] answerArray = answer.split(", ");
                    movieJsonObject.addProperty("movieId", answerArray[0]);
                    movieJsonObject.addProperty("starId", answerArray[1]);
                    movieJsonObject.addProperty("genreId", answerArray[2]);
                }
            }
            movieResultSet.close();
            addMovieStatement.close();
            request.getServletContext().log(movieJsonObject.toString());
            response.getWriter().write(movieJsonObject.toString());
            response.setStatus(200);
        }  catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            request.getServletContext().log("Error:", e);
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}
