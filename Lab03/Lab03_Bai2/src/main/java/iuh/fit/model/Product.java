package iuh.fit.model;


import iuh.fit.state.ProductState;

public class Product {
    private String productId;
    private String productName;
    private double basePrice;
    private ProductState state;

    public Product(String productId, String productName, double basePrice, ProductState state) {
        this.productId = productId;
        this.productName = productName;
        this.basePrice = basePrice;
        this.state = state;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public ProductState getState() {
        return state;
    }

    public void setState(ProductState state) {
        this.state = state;
    }

    public void showTaxPolicy() {
        state.handle(this);
    }
}