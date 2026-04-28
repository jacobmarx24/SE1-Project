package Shop;
import java.util.*;
public class Product {
    private int id;
    private String name;
    private double price;
    private boolean isAvailabile;

    Product(int id, String name, double price, boolean isAvailabile){
        this.id=id;
        this.name=name;
        this.price=price;
        this.isAvailabile = isAvailabile;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public boolean isAvailable(){
        return isAvailabile;
    }
    
    public void setAvailability(boolean isAvailabile) {
        this.isAvailabile = isAvailabile;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
