package iuh.fit.state;


import iuh.fit.model.Product;

public class TaxExemptState implements ProductState {
    @Override
    public void handle(Product product) {
        System.out.println("Sản phẩm [" + product.getProductName() + "] thuộc nhóm MIỄN THUẾ.");
        System.out.println("Không áp dụng bất kỳ loại thuế nào.");
    }

    @Override
    public String getStateName() {
        return "Miễn thuế";
    }
}