package Shop;

import java.util.*;
public class ShoppingCart {
    private  List<Product> products = new ArrayList();
    private double total = 0;


    public List<Product> getProducts() {
        return products;
    }


    public  void setProducts(List<Product> product) {
        products = product;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void addProduct(Product p){
        products.add(p);
    }

    public void deleteProduct(int i){
        products.remove(i);
    }

    public double calculateTotal(){
        double total = 0.0;
        for(Product p: products){
            total+=p.getPrice();
        }
        return total;
    }

    

}
