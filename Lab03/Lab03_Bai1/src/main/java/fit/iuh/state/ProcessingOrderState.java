package fit.iuh.state;


import fit.iuh.model.Order;

public class ProcessingOrderState implements OrderState {

    @Override
    public void handle(Order order) {
        System.out.println("== Đơn hàng ở trạng thái: ĐANG XỬ LÝ ==");
        System.out.println("Tiến hành đóng gói đơn hàng...");
        order.getShippingStrategy().ship(order);

        System.out.println("Đơn hàng đã được xử lý xong -> chuyển sang trạng thái ĐÃ GIAO");
        order.setState(new DeliveredOrderState());
    }

    @Override
    public String getStateName() {
        return "Đang xử lý";
    }
}