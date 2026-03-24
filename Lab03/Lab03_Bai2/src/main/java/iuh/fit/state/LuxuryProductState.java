package iuh.fit.state;


import iuh.fit.model.Product;

public class LuxuryProductState implements ProductState {
    @Override
    public void handle(Product product) {
        System.out.println("Sản phẩm [" + product.getProductName() + "] thuộc nhóm HÀNG XA XỈ.");
        System.out.println("Áp dụng VAT và thuế xa xỉ.");
    }

    @Override
    public String getStateName() {
        return "Hàng xa xỉ";
    }
}