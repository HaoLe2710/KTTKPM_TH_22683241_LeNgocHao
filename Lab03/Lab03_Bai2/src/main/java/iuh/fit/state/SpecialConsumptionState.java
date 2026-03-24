package iuh.fit.state;


import iuh.fit.model.Product;

public class SpecialConsumptionState implements ProductState {
    @Override
    public void handle(Product product) {
        System.out.println("Sản phẩm [" + product.getProductName() + "] thuộc nhóm CHỊU THUẾ TIÊU THỤ.");
        System.out.println("Áp dụng VAT và thuế tiêu thụ.");
    }

    @Override
    public String getStateName() {
        return "Chịu thuế tiêu thụ";
    }
}