package fit.iuh.state;


import fit.iuh.model.Order;

public class DeliveredOrderState implements OrderState {

    @Override
    public void handle(Order order) {
        System.out.println("== Đơn hàng ở trạng thái: ĐÃ GIAO ==");
        System.out.println("Cập nhật hệ thống: đơn hàng " + order.getOrderId() + " đã giao thành công.");
    }

    @Override
    public String getStateName() {
        return "Đã giao";
    }
}