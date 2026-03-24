package iuh.fit;


import iuh.fit.decorator.*;
import iuh.fit.model.Product;
import iuh.fit.state.LuxuryProductState;
import iuh.fit.state.NormalProductState;
import iuh.fit.state.SpecialConsumptionState;
import iuh.fit.state.TaxExemptState;

public class Main {
    public static void main(String[] args) {
        System.out.println("===== KỊCH BẢN 1: SẢN PHẨM THÔNG THƯỜNG =====");
        Product product1 = new Product(
                "P001",
                "Sách giáo khoa",
                100000,
                new NormalProductState()
        );

        product1.showTaxPolicy();
        TaxableItem item1 = new BaseProductItem(product1);
        item1 = new VatTaxDecorator(item1);

        System.out.println(item1.getDescription());
        System.out.println("Tổng giá sau thuế: " + item1.getPrice());
        System.out.println();

        System.out.println("===== KỊCH BẢN 2: SẢN PHẨM CHỊU THUẾ TIÊU THỤ =====");
        Product product2 = new Product(
                "P002",
                "Nước ngọt cao cấp",
                200000,
                new SpecialConsumptionState()
        );

        product2.showTaxPolicy();
        TaxableItem item2 = new BaseProductItem(product2);
        item2 = new VatTaxDecorator(item2);
        item2 = new ConsumptionTaxDecorator(item2);

        System.out.println(item2.getDescription());
        System.out.println("Tổng giá sau thuế: " + item2.getPrice());
        System.out.println();

        System.out.println("===== KỊCH BẢN 3: HÀNG XA XỈ =====");
        Product product3 = new Product(
                "P003",
                "Đồng hồ xa xỉ",
                5000000,
                new LuxuryProductState()
        );

        product3.showTaxPolicy();
        TaxableItem item3 = new BaseProductItem(product3);
        item3 = new VatTaxDecorator(item3);
        item3 = new LuxuryTaxDecorator(item3);

        System.out.println(item3.getDescription());
        System.out.println("Tổng giá sau thuế: " + item3.getPrice());
        System.out.println();

        System.out.println("===== KỊCH BẢN 4: SẢN PHẨM MIỄN THUẾ =====");
        Product product4 = new Product(
                "P004",
                "Thuốc y tế",
                300000,
                new TaxExemptState()
        );

        product4.showTaxPolicy();
        TaxableItem item4 = new BaseProductItem(product4);

        System.out.println(item4.getDescription());
        System.out.println("Tổng giá sau thuế: " + item4.getPrice());
    }
}