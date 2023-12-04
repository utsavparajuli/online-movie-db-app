import classes.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;

@WebServlet(name = "ConfirmationServlet", urlPatterns = "/app/api/confirmation")
public class ConfirmationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/slavemoviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        JsonObject responseJsonObject = new JsonObject();

        JsonArray previousItemsJsonArray = new JsonArray();

        String date = new Date(request.getSession().getLastAccessedTime()).toString();


        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            String query = "SELECT group_concat(s.id) AS SaleID, m.title AS MovieName, COUNT(s.movieID) " +
                                "AS QuantityPurchased FROM moviedb.sales s JOIN moviedb.movies m ON s.movieID = m.id " +
                                "WHERE s.customerID = ? AND s.saleDate = ? GROUP BY m.title;";

            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, session.getAttribute("customerId").toString());
            statement.setString(2, date);
            ResultSet resultSet = statement.executeQuery();

            // Iterate through each row of resultSet
            while (resultSet.next()) {
                JsonObject jsonObject = new JsonObject();

                jsonObject.addProperty("sale_ids", resultSet.getString("SaleID"));
                jsonObject.addProperty("movie_name", resultSet.getString("MovieName"));
                jsonObject.addProperty("quantity", resultSet.getString("QuantityPurchased"));

                previousItemsJsonArray.add(jsonObject);
            }
            resultSet.close();
            statement.close();

            responseJsonObject.add("sales", previousItemsJsonArray);
            responseJsonObject.addProperty("total", user.getLastPurchaseCost());

            // write all the data into the jsonObject
            response.getWriter().write(responseJsonObject.toString());

            // Write JSON string to output
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            response.getWriter().close();
        }
    }
}
