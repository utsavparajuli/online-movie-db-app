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

@WebServlet(name = "SubmitStarServlet", urlPatterns = "/_dashboard/api/star")
public class SubmitStarServlet extends HttpServlet {
    public static final String starIdQuery = "SELECT MAX(id) AS id FROM stars;";
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/mastermoviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private String getInsertString(String starDob) {
        String insertString = "INSERT INTO stars ";
        if (starDob != null)
            insertString += "VALUES(?, ?, ?);";
        else
            insertString += "VALUES(?, ? , null); ";
        return insertString;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject starJsonObject = new JsonObject();
        PrintWriter out = response.getWriter();
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(starIdQuery);
            ResultSet starIdResultSet = statement.executeQuery();

            String newStarId = "";
            while(starIdResultSet.next()) {
                String maxStarId = starIdResultSet.getString("id");
                newStarId = "nm" + (Integer.parseInt(maxStarId.substring(2)) + 1);
            }

            starIdResultSet.close();
            statement.close();
            starJsonObject.addProperty("id", newStarId);
            out.write(starJsonObject.toString());
            response.setStatus(200);
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            request.getServletContext().log("Error:", e);
            response.setStatus(500);
        } finally {
            out.close();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String starId = request.getParameter("star_id");
        String starName = request.getParameter("star_name");
        String starDob = request.getParameter("star_dob");
        String insertString = getInsertString(starDob);
        JsonObject starJsonObject = new JsonObject();
        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement insertStatement = conn.prepareStatement(insertString);
            insertStatement.setString(1, starId);
            insertStatement.setString(2, starName);
            if (starDob != null)
                insertStatement.setString(3, starDob);

            var updateResponse = insertStatement.executeUpdate();
            if(updateResponse == 1) {
                request.getServletContext().log("successfully inserted " + starId);
                starJsonObject.addProperty("check", "success");
            } else {
                request.getServletContext().log("unable to insert star");
            }
            insertStatement.close();
            response.getWriter().write(starJsonObject.toString());
            response.setStatus(200);

        } catch (Exception e) {
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
