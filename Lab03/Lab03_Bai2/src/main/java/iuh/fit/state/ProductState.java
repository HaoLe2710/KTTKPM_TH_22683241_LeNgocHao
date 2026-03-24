package iuh.fit.state;


import iuh.fit.model.Product;

public interface ProductState {
    void handle(Product product);
    String getStateName();
}