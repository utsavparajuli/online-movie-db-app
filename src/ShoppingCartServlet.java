import classes.MovieItem;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;


@WebServlet(name = "CartServlet", urlPatterns = "/app/api/cart")
public class ShoppingCartServlet extends HttpServlet {
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
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        JsonObject responseJsonObject = new JsonObject();

        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
        if (previousItems == null) {
            previousItems = new ArrayList<String>();
        }
        // Log to localhost log
        request.getServletContext().log("getting " + previousItems.size() + " items");
        JsonArray previousItemsJsonArray = new JsonArray();
        //used to add all the items in previousItems to previousItemsJsonArray
        //previousItems.forEach(previousItemsJsonArray::add);

        Map<String, Long> movieCounts = previousItems.stream()
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));


        //to build a new cart everytime
        if(user != null) {
            user.clearCart();
        }

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            for (Map.Entry<String, Long> entry : movieCounts.entrySet()) {
                String query = "SELECT m.id, m.title, m.price FROM movies m WHERE m.id = ?;";

                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, entry.getKey());
                ResultSet resultSet = statement.executeQuery();

                // Iterate through each row of resultSet
                while (resultSet.next()) {
                    JsonObject jsonObject = new JsonObject();
                    String movieId = resultSet.getString("id");

                    if (user.getCartItems().containsKey(movieId)) {
                        user.getCartItems().get(movieId).setQuantity(user.getCartItems().get(movieId).getQuantity() + 1);
                    }
                    else {
                        user.addMovie(new MovieItem(movieId, resultSet.getString("title"),
                                Double.parseDouble(resultSet.getString("price")), entry.getValue().intValue()));
                    }

                    jsonObject.addProperty("movie_id", movieId);
                    jsonObject.addProperty("movie_title", resultSet.getString("title"));
                    jsonObject.addProperty("quantity", entry.getValue());
                    jsonObject.addProperty("price", (resultSet.getString("price")));

                    previousItemsJsonArray.add(jsonObject);
                }
                resultSet.close();
                statement.close();
            }
            request.getSession().setAttribute("user", user);
            responseJsonObject.add("previousItems", previousItemsJsonArray);

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

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String item = request.getParameter("item");
        String  updateType = request.getParameter("type");
        String buttonType = request.getParameter("submit");

        String typeOfCartUpdate = item.substring(item.length() - 1);

        if (item.length() > 0) {
            item = item.substring(0, item.length() - 1);
            System.out.println(item); // Output: "Hello, World"
        } else {
            // Handle the case when the string is empty (no characters to remove).
            System.out.println("The string is empty.");
        }

        System.out.println(updateType);

        request.getParameterMap();

        System.out.println(item);
        System.out.println(typeOfCartUpdate);

        HttpSession session = request.getSession();

        // get the previous items in a ArrayList
        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");

        if(typeOfCartUpdate.equalsIgnoreCase("+") ) {
            if (previousItems == null) {
                previousItems = new ArrayList<String>();
                previousItems.add(item);
                session.setAttribute("previousItems", previousItems);
            } else {
                // prevent corrupted states through sharing under multi-threads
                // will only be executed by one thread at a time
                synchronized (previousItems) {
                    previousItems.add(item);
                }
            }
        }
        else {
            synchronized (previousItems) {
                previousItems.remove(item);
            }
        }
        JsonObject responseJsonObject = new JsonObject();

        JsonArray previousItemsJsonArray = new JsonArray();
        previousItems.forEach(previousItemsJsonArray::add);
        responseJsonObject.add("previousItems", previousItemsJsonArray);

        response.getWriter().write(responseJsonObject.toString());
    }
}
