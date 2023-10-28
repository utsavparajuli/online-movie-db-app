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
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {

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
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject responseJsonObject = new JsonObject();


        JsonArray previousItemsJsonArray = new JsonArray();

        User user = (User) request.getSession().getAttribute("user");



        double price = 0;

        for (Map.Entry<String, Movie> entry : user.getCartItems().entrySet()) {
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


            // write all the data into the jsonObject
            response.getWriter().write(responseJsonObject.toString());



            // Write JSON string to output
            // Set response status to 200 (OK)
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
                    "WHERE cc.firstName = '" + first_name + "' AND cc.lastName = '" + last_name + "'" +
                    " AND cc.id = '" + cc_number + "' AND cc.expiration = '" + exp_date + "';";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            // TODO: check if it is null
            if (rs.next()) {
                // set this user into the session
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

                String customerId = (String) request.getSession().getAttribute("customerId");
                String movieId = null;
                String date = new Date(request.getSession().getLastAccessedTime()).toString();


                //Posting it to the system in the sales table

                ArrayList<String> previousItems = (ArrayList<String>) request.getSession().getAttribute("previousItems");


                int numberofMoviesrecorded = 0;

                for (String s :
                        previousItems) {
                    movieId = s;

                    String saleEntryQuery = "INSERT INTO sales VALUES (null, " + customerId + ", '" + movieId + "', '" + date + "');";

                    PreparedStatement insertStatement = conn.prepareStatement(saleEntryQuery);

                    var updateResponse = insertStatement.executeUpdate();



                    if(updateResponse == 1) {
                        System.out.println("sale recorded");
                        numberofMoviesrecorded++;
                    }
                    else {
                        System.out.println("sale not recorded");
                    }
                    insertStatement.close();
                }
                responseJsonObject.addProperty("recorded", numberofMoviesrecorded);



//                PreparedStatement salePStatement = conn.prepareStatement();
            }
            else {
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("payment failed");

                // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
                responseJsonObject.addProperty("message", "Wrong information");
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
        }    }
}
