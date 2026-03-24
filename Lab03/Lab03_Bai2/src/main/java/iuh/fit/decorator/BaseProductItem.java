package iuh.fit.decorator;


import iuh.fit.model.Product;

public class BaseProductItem implements TaxableItem {
    private Product product;

    public BaseProductItem(Product product) {
        this.product = product;
    }

    @Override
    public double getPrice() {
        return product.getBasePrice();
    }

    @Override
    public String getDescription() {
        return product.getProductName() + " - Giá gốc: " + product.getBasePrice();
    }

    public Product getProduct() {
        return product;
    }
}