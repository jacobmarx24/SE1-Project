package Shop;
import java.util.*;
public class Product {
    private int id;
    private String name;
    private double price;
    private int inStock;

    Product(int id, String name, double price, int inStock){
        this.id=id;
        this.name=name;
        this.price=price;
        this.inStock = inStock;
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

    public int getInStock() {
        return inStock;
    }

    public void setInStock(int inStock) {
        this.inStock = inStock;
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

    public String getCSV(){
        return Integer.toString(id) + "," + name + "," + Double.toString(price)
        + "," + Integer.toString(inStock);
    }
}
