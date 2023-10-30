import java.util.ArrayList;
import java.util.HashMap;

/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
public class User {

    private final String username;
    private HashMap<String, Movie> cartItems;
    private String movieId;
    private String movieName;


    public User(String username) {
        this.username = username;
        cartItems = new HashMap<>();
    }

    public void clearCart() {
        cartItems = new HashMap<>();
    }

    public void addMovie(Movie m) {
        cartItems.put(m.getId(), m);
    }

    public void removeMovie(Movie m) {
        cartItems.remove(m.getId());
    }

    public HashMap<String, Movie> getCartItems() {
        return cartItems;
    }


    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }


    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getMovieId() {
        return movieId;
    }
}