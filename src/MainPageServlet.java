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
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


@WebServlet(name = "MainPageServlet", urlPatterns = "/app/api/mainpage")
public class MainPageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    //private static final String movieTitleIdQuery = "CALL movie_title_id_query(?)";
    private static final String movieTitleIdQuery = "CALL movie_title_id_query(?)";
    private static final String genreQuery = "SELECT G.id, G.name " +
                                                "FROM genres G " +
                                                "ORDER BY G.name ASC; ";
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/slavemoviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getParameter("autocomplete") != null) {
            titleId(request, response);
            return;
        }
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(genreQuery);
            ResultSet resultSet  = statement.executeQuery();
            JsonArray jsonArray = new JsonArray();
            while (resultSet.next()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("genre_id", resultSet.getString("id"));
                jsonObject.addProperty("genre_name", resultSet.getString("name"));
                jsonArray.add(jsonObject);
            }
            resultSet.close();
            statement.close();
            request.getServletContext().log("getting " + jsonArray.size() + " results");
            out.write(jsonArray.toString());
            response.setStatus(200);
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
    protected void titleId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String movieTitle = request.getParameter("autocomplete");
        JsonArray titleResultArray = new JsonArray();
        if (movieTitle == null || movieTitle.trim().isEmpty()) {
            response.getWriter().write(titleResultArray.toString());
            return;
        }
        try (Connection conn = dataSource.getConnection()) {
            String[] titleArray = request.getParameter("autocomplete")
                    .split("[\\p{IsPunctuation}\\s]+");
            StringBuilder titleQuery = new StringBuilder();;
            for (String s : titleArray) {
                titleQuery.append("+").append(s).append("* ");
            }
            PreparedStatement titleQueryStatement = conn.prepareStatement(movieTitleIdQuery);
            titleQueryStatement.setString(1, titleQuery.toString());
            request.getServletContext().log(titleQueryStatement.toString());
            ResultSet titleResultSet = titleQueryStatement.executeQuery();
            while (titleResultSet.next()) {
                titleResultArray.add(generateJsonObject(
                        titleResultSet.getString("id"),
                        titleResultSet.getString("title")));
            }
            request.getServletContext().log("4");
            titleResultSet.close();
            titleQueryStatement.close();
            out.write(titleResultArray.toString());
            response.setStatus(200);
        }  catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus(500);
        } finally {
            out.close();
        }
    }

    private static JsonObject generateJsonObject(String movieID, String movieTitle) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", movieTitle);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("movieID", movieID);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }
}

