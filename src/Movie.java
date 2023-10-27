public class Movie {
    private final String id;
    private final String title;
    private int quantity;
    private final int price;

    public Movie(String id, String title) {
        this.id = id;
        this.title = title;
        this.price = 1;
        quantity = 1;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }
}
