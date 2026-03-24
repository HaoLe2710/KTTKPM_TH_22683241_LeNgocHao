package fit.iuh.strategy.shipping;


import fit.iuh.model.Order;

public class StandardShippingStrategy implements ShippingStrategy {
    @Override
    public void ship(Order order) {
        System.out.println("Vận chuyển bằng hình thức GIAO HÀNG THƯỜNG cho đơn " + order.getOrderId());
    }
}