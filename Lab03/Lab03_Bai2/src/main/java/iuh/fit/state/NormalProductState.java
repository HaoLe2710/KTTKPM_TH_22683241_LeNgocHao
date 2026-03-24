package iuh.fit.state;


import iuh.fit.model.Product;

public class NormalProductState implements ProductState {
    @Override
    public void handle(Product product) {
        System.out.println("Sản phẩm [" + product.getProductName() + "] thuộc nhóm HÀNG THÔNG THƯỜNG.");
        System.out.println("Áp dụng thuế VAT cơ bản.");
    }

    @Override
    public String getStateName() {
        return "Hàng thông thường";
    }
}