package fit.iuh.state;


import fit.iuh.model.Order;

public class CancelledOrderState implements OrderState {

    @Override
    public void handle(Order order) {
        System.out.println("== Đơn hàng ở trạng thái: HỦY ==");
        System.out.println("Thực hiện hủy đơn hàng: " + order.getOrderId());
        order.getRefundStrategy().refund(order);
    }

    @Override
    public String getStateName() {
        return "Hủy";
    }
}