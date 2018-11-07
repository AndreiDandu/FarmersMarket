package dandu.andrei.farmersmarket.ListViee;

public class Ad {
    private String description;
    private int price;
    private int quantity;
    public Ad(){}
    public Ad(String description, int price, int quantity) {
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }
    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
