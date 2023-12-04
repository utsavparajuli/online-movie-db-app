import classes.MovieItem;
import classes.User;
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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Map;


@WebServlet(name = "PaymentServlet", urlPatterns = "/app/api/payment")
public class PaymentServlet extends HttpServlet {

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/mastermoviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject responseJsonObject = new JsonObject();
        JsonArray  previousItemsJsonArray = new JsonArray();
        User       user = (User) request.getSession().getAttribute("user");
        double     price = 0;

        for (Map.Entry<String, MovieItem> entry : user.getCartItems().entrySet()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("movie_id", entry.getValue().getId());
            jsonObject.addProperty("movie_title", entry.getValue().getTitle());
            jsonObject.addProperty("quantity", entry.getValue().getQuantity());
            jsonObject.addProperty("price", entry.getValue().getPrice());

            var totalPerMovie = entry.getValue().getPrice() * entry.getValue().getQuantity();
            price += totalPerMovie;

            previousItemsJsonArray.add(jsonObject);
        }

        responseJsonObject.add("previousItems", previousItemsJsonArray);
        responseJsonObject.addProperty("total", price);

        user.setLastPurchaseCost(price);
        request.getSession().setAttribute("user", user);

        response.getWriter().write(responseJsonObject.toString());
        response.setStatus(200);

    }

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String first_name = request.getParameter("first_name");
        String last_name = request.getParameter("last_name");
        String cc_number = request.getParameter("cc_number");
        String exp_date = request.getParameter("cc_expiration");

        JsonObject responseJsonObject = new JsonObject();

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT * FROM creditcards as cc " +
                    "WHERE cc.firstName = ? AND " +
                    "cc.lastName = ? AND cc.id = ? " +
                    "AND cc.expiration = ?;";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, first_name);
            statement.setString(2, last_name);
            statement.setString(3, cc_number);
            statement.setString(4, exp_date);
            // Perform the query
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

                String customerId = (String) request.getSession().getAttribute("customerId");
                String movieId;
                String date = new Date(request.getSession().getLastAccessedTime()).toString();

                ArrayList<String> previousItems = (ArrayList<String>) request.getSession().getAttribute("previousItems");


                int numberMovies = 0;

                for (String s :
                        previousItems) {
                    movieId = s;
                    String saleEntryQuery = "INSERT INTO sales VALUES (null, ?, ?, ?);";

                    PreparedStatement insertStatement = conn.prepareStatement(saleEntryQuery);
                    insertStatement.setString(1, customerId);
                    insertStatement.setString(1, movieId);
                    insertStatement.setString(2, date);
                    var updateResponse = insertStatement.executeUpdate();

                    if(updateResponse == 1) {
                        System.out.println("sale recorded");
                        numberMovies++;
                    }
                    else {
                        System.out.println("sale not recorded");
                    }
                    insertStatement.close();
                }
                responseJsonObject.addProperty("recorded", numberMovies);

                User       user = (User) request.getSession().getAttribute("user");
                user.clearCart();

                request.getSession().setAttribute("user", user);
                request.getSession().setAttribute("previousItems", new ArrayList<String>());
            }
            else {
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("payment failed");
                responseJsonObject.addProperty("message", "Could not validate credentials / Try Again");
            }
            rs.close();
            statement.close();
            response.setStatus(200);
            response.getWriter().write(responseJsonObject.toString());
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
    }
}